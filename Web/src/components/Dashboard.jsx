import React, { useEffect, useState } from 'react';
import { collection, query, orderBy, limit, getDocs } from 'firebase/firestore';
import { db } from '../lib/firebase';
import { Activity, Dumbbell, CalendarDays, Flame } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function Dashboard() {
  const [workouts, setWorkouts] = useState([]);
  const [loading, setLoading] = useState(true);

  // In a real app we'd filter by user ID.
  useEffect(() => {
    async function fetchWorkouts() {
      try {
        const q = query(collection(db, "Workouts"), orderBy("date", "desc"), limit(5));
        const querySnapshot = await getDocs(q);
        const data = querySnapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
        setWorkouts(data);
      } catch (err) {
        console.error("Firebase not fully configured yet, showing empty state", err);
      }
      setLoading(false);
    }
    fetchWorkouts();
  }, []);

  return (
    <div className="animate-fade-in">
      <h1 style={{ fontSize: '2.5rem', marginBottom: '2rem' }}>Welcome Back, Athlete!</h1>
      
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '1.5rem', marginBottom: '3rem' }}>
        <div className="glass-panel" style={{ padding: '1.5rem', display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <div style={{ background: 'rgba(139, 92, 246, 0.2)', padding: '1rem', borderRadius: '50%', color: '#8b5cf6' }}>
            <Activity size={24} />
          </div>
          <div>
            <div style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Workouts This Week</div>
            <div style={{ fontSize: '1.5rem', fontWeight: 'bold' }}>4</div>
          </div>
        </div>
        
        <div className="glass-panel" style={{ padding: '1.5rem', display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <div style={{ background: 'rgba(59, 130, 246, 0.2)', padding: '1rem', borderRadius: '50%', color: '#3b82f6' }}>
            <Flame size={24} />
          </div>
          <div>
            <div style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Current Streak</div>
            <div style={{ fontSize: '1.5rem', fontWeight: 'bold' }}>12 Days</div>
          </div>
        </div>
      </div>

      <h2 style={{ fontSize: '1.5rem', marginBottom: '1.5rem' }}>Recent Activity</h2>
      
      {loading ? (
        <div style={{ textAlign: 'center', padding: '3rem' }}>Loading...</div>
      ) : workouts.length > 0 ? (
        <div style={{ display: 'grid', gap: '1rem' }}>
          {workouts.map(w => (
            <div key={w.id} className="glass-panel" style={{ padding: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <h3 style={{ margin: '0 0 0.5rem 0', fontSize: '1.2rem' }}>{w.name}</h3>
                <div style={{ display: 'flex', gap: '1rem', color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}><CalendarDays size={16}/> {new Date(w.date?.seconds * 1000 || Date.now()).toLocaleDateString()}</span>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}><Dumbbell size={16}/> {w.exercises?.length || 0} Exercises</span>
                </div>
              </div>
              <button className="btn-primary" style={{ padding: '0.5rem 1rem', fontSize: '0.9rem' }}>View</button>
            </div>
          ))}
        </div>
      ) : (
        <div className="glass-panel" style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
          <Dumbbell size={48} style={{ margin: '0 auto 1rem', opacity: 0.5 }} />
          <h3>No Workouts Yet</h3>
          <p>You haven't logged any workouts to the Cloud Database yet.</p>
          <p style={{ fontSize: '0.8rem', marginTop: '1rem' }}>Make sure to fill in your Firebase API Keys in `.env`!</p>
        </div>
      )}
    </div>
  );
}
