import React, { useEffect, useState } from 'react';
import { auth } from '../lib/firebase';
import { Clock, Calendar, Activity } from 'lucide-react';

export default function History() {
  const [workouts, setWorkouts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchHistory() {
      if (!auth.currentUser) return;
      try {
        const response = await fetch(`http://localhost:5000/api/workouts?userId=${auth.currentUser.uid}`);
        if (response.ok) {
          const data = await response.json();
          setWorkouts(data);
        } else {
          console.error("Failed to fetch workouts");
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
    fetchHistory();
  }, []);

  return (
    <div className="animate-fade-in" style={{ paddingBottom: '2rem' }}>
      <h1 style={{ fontSize: '2rem', fontWeight: 'bold', color: 'white', marginBottom: '1.5rem' }}>Workout History</h1>
      
      {loading ? (
        <div style={{ color: '#a1a1aa' }}>Loading history...</div>
      ) : workouts.length === 0 ? (
        <div className="glass-panel" style={{ padding: '3rem 2rem', textAlign: 'center', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <Activity size={48} color="#3f3f46" style={{ marginBottom: '1rem' }} />
          <h2 style={{ fontSize: '1.5rem', color: 'white', marginBottom: '0.5rem' }}>No Workouts Yet</h2>
          <p style={{ color: '#a1a1aa' }}>Your completed workouts will appear here.</p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {workouts.map(workout => (
            <div key={workout.id} className="glass-panel" style={{ padding: '1.5rem', borderLeft: '4px solid #8b5cf6' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
                <div>
                  <h3 style={{ fontSize: '1.25rem', fontWeight: 'bold', color: 'white' }}>{workout.name}</h3>
                  <div style={{ display: 'flex', gap: '1rem', marginTop: '0.5rem', color: '#a1a1aa', fontSize: '0.9rem' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
                      <Calendar size={16} />
                      {new Date(workout.date).toLocaleDateString()}
                    </div>
                  </div>
                </div>
                <div style={{ background: 'rgba(139, 92, 246, 0.2)', color: '#8b5cf6', padding: '0.25rem 0.75rem', borderRadius: '1rem', fontSize: '0.875rem', fontWeight: 'bold' }}>
                  {workout.exercises?.length || 0} Exercises
                </div>
              </div>

              {workout.exercises && workout.exercises.length > 0 && (
                <div style={{ marginTop: '1rem', borderTop: '1px solid #27272a', paddingTop: '1rem' }}>
                  {workout.exercises.map((ex, idx) => (
                    <div key={idx} style={{ display: 'flex', justifyContent: 'space-between', color: '#e4e4e7', marginBottom: '0.5rem', fontSize: '0.95rem' }}>
                      <span><span style={{ color: '#8b5cf6', marginRight: '0.5rem' }}>{idx + 1}.</span>{ex.exerciseName}</span>
                      <span style={{ color: '#a1a1aa' }}>
                        {ex.sets?.length || 0} sets
                        {ex.sets && ex.sets.length > 0 && ` â€¢ Top: ${Math.max(...ex.sets.map(s => s.weight))} kg`}
                      </span>
                    </div>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
