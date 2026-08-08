import React, { useEffect, useState } from 'react';
import { auth } from '../lib/firebase';
import { Activity, Dumbbell, CalendarDays, Flame } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import MuscleHeatmap from './MuscleHeatmap';

export default function Dashboard() {
  const [workouts, setWorkouts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [userName, setUserName] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    if (auth.currentUser?.displayName) {
      setUserName(auth.currentUser.displayName.split(" ")[0]);
    }
  }, []);

  useEffect(() => {
    async function fetchWorkouts() {
      if (!auth.currentUser) return;
      try {
        const response = await fetch(`http://localhost:5000/api/workouts?userId=${auth.currentUser.uid}`);
        if (response.ok) {
          const data = await response.json();
          setWorkouts(data);
        } else {
          console.error("Failed to fetch workouts from backend API");
        }
      } catch (err) {
        console.error("Backend API not reachable, showing empty state", err);
      }
      setLoading(false);
    }
    
    // Only run when auth.currentUser is populated
    if (auth.currentUser) {
      fetchWorkouts();
    }
  }, [auth.currentUser]);

  return (
    <div className="animate-fade-in" style={{ maxWidth: '800px', margin: '0 auto' }}>
      <h1 style={{ fontSize: '2rem', marginBottom: '2rem', fontWeight: 'bold' }}>Ready to crush it{userName ? `, ${userName}` : ''}?</h1>
      
      {/* Quick Stats Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '2rem' }}>
        
        {/* Workouts Card */}
        <div className="glass-panel" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem', color: '#F59E0B' }}>
            <Activity size={18} />
            <span style={{ fontSize: '0.9rem', fontWeight: 600 }}>Workouts</span>
          </div>
          <div style={{ fontSize: '2.5rem', fontWeight: 'bold', marginTop: 'auto' }}>{workouts.length}</div>
          <div style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginTop: '0.2rem' }}>Recent sessions</div>
        </div>
        
        {/* Balance Card */}
        <div className="glass-panel" onClick={() => navigate('/musclemap')} style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', cursor: 'pointer', position: 'relative' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem', color: '#3B82F6' }}>
            <Activity size={18} />
            <span style={{ fontSize: '1rem', fontWeight: 600 }}>Balance</span>
          </div>
          <div style={{ display: 'flex', gap: '1rem', height: '140px', marginTop: 'auto', justifyContent: 'center' }}>
            <div style={{ flex: 1, display: 'flex', justifyContent: 'center' }}><MuscleHeatmap viewMode="front" getMuscleColor={() => '#374151'} /></div>
            <div style={{ flex: 1, display: 'flex', justifyContent: 'center' }}><MuscleHeatmap viewMode="back" getMuscleColor={() => '#374151'} /></div>
          </div>
          <div style={{ color: '#3B82F6', fontSize: '0.9rem', marginTop: '1rem', textAlign: 'center', fontWeight: 'bold' }}>View Full Map &rarr;</div>
        </div>

      </div>

      {/* Action Buttons */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', marginBottom: '2.5rem' }}>
        <button 
          onClick={() => navigate('/select-muscle')}
          style={{ width: '100%', padding: '1.2rem', background: '#3B82F6', color: 'white', border: 'none', borderRadius: '1rem', fontSize: '1.1rem', fontWeight: 'bold', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem' }}
        >
          <span style={{ transform: 'rotate(90deg)' }}>▲</span> Start New Workout
        </button>
        <button 
          onClick={() => navigate('/ai')}
          className="glass-panel"
          style={{ width: '100%', padding: '1rem', color: 'white', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', fontSize: '1rem', fontWeight: '600' }}
        >
          <span style={{ color: '#60A5FA' }}>✨</span> AI Workout
        </button>
        <button 
          onClick={() => navigate('/diet')}
          className="glass-panel"
          style={{ width: '100%', padding: '1rem', color: 'white', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', fontSize: '1rem', fontWeight: '600', borderColor: 'rgba(16, 185, 129, 0.2)' }}
        >
          <span style={{ color: '#10B981' }}>✨</span> AI Diet Plan
        </button>
        <button 
          onClick={() => navigate('/analytics')}
          className="glass-panel"
          style={{ width: '100%', padding: '1rem', color: 'white', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', fontSize: '1rem', fontWeight: '600', borderColor: 'rgba(139, 92, 246, 0.2)' }}
        >
          <span style={{ color: '#8B5CF6' }}>ⓘ</span> View Analytics & Progress
        </button>
      </div>

      <h2 style={{ fontSize: '1.2rem', marginBottom: '1rem' }}>Recent Workouts</h2>
      
      {loading ? (
        <div style={{ textAlign: 'center', padding: '3rem' }}>Loading...</div>
      ) : workouts.length > 0 ? (
        <div style={{ display: 'grid', gap: '1rem' }}>
          {workouts.slice(0, 5).map(w => (
            <div key={w.id} className="glass-panel" style={{ padding: '1.2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <h3 style={{ margin: '0 0 0.2rem 0', fontSize: '1.1rem' }}>{w.name}</h3>
                <div style={{ display: 'flex', gap: '1rem', color: 'var(--text-muted)', fontSize: '0.8rem' }}>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}><CalendarDays size={14}/> {new Date(w.date?._seconds * 1000 || Date.now()).toLocaleDateString()}</span>
                  <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}><Dumbbell size={14}/> {w.exercises?.length || 0} Exercises</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="glass-panel" style={{ padding: '2rem', textAlign: 'center', color: 'var(--text-muted)' }}>
          <Dumbbell size={32} style={{ margin: '0 auto 1rem', opacity: 0.5 }} />
          <p>No recent workouts found for this account.</p>
        </div>
      )}
    </div>
  );
}
