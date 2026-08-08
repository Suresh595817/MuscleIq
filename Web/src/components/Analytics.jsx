import React, { useEffect, useState } from 'react';
import { LineChart, Line, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { TrendingUp, Activity } from 'lucide-react';
import { collection, query, where, getDocs, orderBy } from 'firebase/firestore';
import { db, auth } from '../lib/firebase';

const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    return (
      <div className="glass-panel" style={{ padding: '1rem', border: '1px solid rgba(139, 92, 246, 0.5)' }}>
        <p style={{ margin: '0 0 0.5rem 0', fontWeight: 'bold' }}>{label}</p>
        <p style={{ margin: 0, color: '#8b5cf6' }}>
          {payload[0].value} kg
        </p>
      </div>
    );
  }
  return null;
};

export default function Analytics() {
  const [strengthData, setStrengthData] = useState([]);
  const [volumeData, setVolumeData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchData() {
      if (!auth.currentUser) return;
      try {
        const q = query(
          collection(db, "Workouts"),
          where("userId", "==", auth.currentUser.uid)
        );
        const snapshot = await getDocs(q);
        const workouts = snapshot.docs.map(doc => doc.data());

        // Sort workouts in memory (asc)
        workouts.sort((a, b) => {
          const dateA = a.date?.seconds || 0;
          const dateB = b.date?.seconds || 0;
          return dateA - dateB;
        });

        // Process Volume (last 7 days of week)
        const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
        const volMap = { 'Mon': 0, 'Tue': 0, 'Wed': 0, 'Thu': 0, 'Fri': 0, 'Sat': 0, 'Sun': 0 };
        
        const getStartOfWeek = (d) => {
          const date = new Date(d);
          const day = date.getDay();
          const diff = date.getDate() - day + (day === 0 ? -6 : 1); // adjust when day is sunday
          return new Date(date.setDate(diff)).setHours(0,0,0,0);
        };
        const currentWeekStart = getStartOfWeek(new Date());

        // Process Strength (Max weight lifted over time)
        const strData = [];

        workouts.forEach(w => {
          // Volume
          const date = new Date(w.date?.seconds * 1000 || Date.now());
          const dayName = days[date.getDay()];
          
          let workoutVolume = 0;
          let maxWeight = 0;

          if (w.exercises) {
            w.exercises.forEach(ex => {
              if (ex.sets) {
                ex.sets.forEach(set => {
                  workoutVolume += (set.weight || 0) * (set.reps || 0);
                  if (set.weight > maxWeight) maxWeight = set.weight;
                });
              }
            });
          }
          
          if (date.getTime() >= currentWeekStart) {
            volMap[dayName] += workoutVolume;
          }
          
          // Strength progression
          strData.push({
            date: date.toLocaleDateString(),
            weight: maxWeight
          });
        });

        setVolumeData([
          { day: 'Mon', volume: volMap['Mon'] },
          { day: 'Tue', volume: volMap['Tue'] },
          { day: 'Wed', volume: volMap['Wed'] },
          { day: 'Thu', volume: volMap['Thu'] },
          { day: 'Fri', volume: volMap['Fri'] },
          { day: 'Sat', volume: volMap['Sat'] },
          { day: 'Sun', volume: volMap['Sun'] }
        ]);

        // If no strength data, put placeholder
        setStrengthData(strData.length > 0 ? strData : [{ date: 'No Data', weight: 0 }]);

      } catch (err) {
        console.error("Error fetching analytics", err);
      }
      setLoading(false);
    }
    fetchData();
  }, []);

  return (
    <div className="animate-fade-in">
      <h1 style={{ fontSize: '2.5rem', marginBottom: '2rem' }}>Analytics & Progress</h1>
      
      {loading ? (
        <div style={{ textAlign: 'center', padding: '3rem' }}>Loading...</div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '2rem' }}>
        {/* Strength Progression (1RM) */}
        <div className="glass-panel" style={{ padding: '2rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '2rem' }}>
            <div style={{ background: 'rgba(139, 92, 246, 0.2)', padding: '0.75rem', borderRadius: '50%', color: '#8b5cf6' }}>
              <TrendingUp size={24} />
            </div>
            <div>
              <h2 style={{ margin: 0, fontSize: '1.25rem' }}>Estimated 1-Rep Max</h2>
              <div style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Deadlift Progression (Last 6 Months)</div>
            </div>
          </div>
          
          <div style={{ height: '300px', width: '100%' }}>
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={strengthData}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.1)" vertical={false} />
                <XAxis dataKey="date" stroke="var(--text-muted)" tick={{fill: 'var(--text-muted)'}} axisLine={false} tickLine={false} />
                <YAxis stroke="var(--text-muted)" tick={{fill: 'var(--text-muted)'}} axisLine={false} tickLine={false} />
                <Tooltip content={<CustomTooltip />} />
                <Line 
                  type="monotone" 
                  dataKey="weight" 
                  stroke="#8b5cf6" 
                  strokeWidth={4}
                  dot={{ fill: '#8b5cf6', strokeWidth: 2, r: 6, stroke: '#1e293b' }}
                  activeDot={{ r: 8, strokeWidth: 0 }}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Volume Load */}
        <div className="glass-panel" style={{ padding: '2rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '2rem' }}>
            <div style={{ background: 'rgba(59, 130, 246, 0.2)', padding: '0.75rem', borderRadius: '50%', color: '#3b82f6' }}>
              <Activity size={24} />
            </div>
            <div>
              <h2 style={{ margin: 0, fontSize: '1.25rem' }}>Weekly Volume Load</h2>
              <div style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Total weight lifted this week</div>
            </div>
          </div>
          
          <div style={{ height: '300px', width: '100%' }}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={volumeData}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.1)" vertical={false} />
                <XAxis dataKey="day" stroke="var(--text-muted)" tick={{fill: 'var(--text-muted)'}} axisLine={false} tickLine={false} />
                <YAxis stroke="var(--text-muted)" tick={{fill: 'var(--text-muted)'}} axisLine={false} tickLine={false} />
                <Tooltip content={<CustomTooltip />} cursor={{fill: 'rgba(255,255,255,0.05)'}} />
                <Bar dataKey="volume" fill="#3b82f6" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
        </div>
      )}
    </div>
  );
}
