import React from 'react';
import { ArrowLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const muscleGroups = [
  { id: 'Chest', label: 'Chest', image: '/images/chest.jpg' },
  { id: 'Back', label: 'Back', image: '/images/back.jpg' },
  { id: 'Legs', label: 'Legs', image: '/images/legs.jpg' },
  { id: 'Shoulders', label: 'Shoulders', image: '/images/shoulders.jpg' },
  { id: 'Biceps', label: 'Biceps', image: '/images/biceps.jpg' },
  { id: 'Triceps', label: 'Triceps', image: '/images/triceps.jpg' },
  { id: 'Core', label: 'Core', image: '/images/core.jpg' },
  { id: 'Forearms', label: 'Forearms', image: '/images/forearms.jpg' }
];

export default function SelectMuscleGroup() {
  const navigate = useNavigate();

  const handleSelect = (muscleId) => {
    let mappedMuscle = muscleId;
    if (muscleId === 'Back') mappedMuscle = 'Lats';
    if (muscleId === 'Legs') mappedMuscle = 'Quads';
    if (muscleId === 'Shoulders') mappedMuscle = 'Front Delts';
    if (muscleId === 'Core') mappedMuscle = 'Abs';

    navigate('/track', { state: { selectedMuscle: mappedMuscle } });
  };

  return (
    <div className="animate-fade-in" style={{ maxWidth: '800px', margin: '0 auto' }}>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '2rem' }}>
        <button onClick={() => navigate(-1)} style={{ background: 'transparent', border: 'none', color: 'white', cursor: 'pointer', display: 'flex', alignItems: 'center' }}>
          <ArrowLeft size={24} style={{ marginRight: '1rem' }} />
        </button>
        <h1 style={{ fontSize: '1.5rem', margin: 0, fontWeight: 'bold' }}>Select Muscle Group</h1>
      </div>

      {/* Grid of Muscles */}
      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: 'repeat(auto-fill, minmax(150px, 1fr))', 
        gap: '1rem' 
      }}>
        {muscleGroups.map((muscle) => (
          <div 
            key={muscle.id}
            onClick={() => handleSelect(muscle.id)}
            style={{ 
              position: 'relative', 
              cursor: 'pointer', 
              borderRadius: '1rem', 
              overflow: 'hidden',
              aspectRatio: '1',
              boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)'
            }}
          >
            <img 
              src={`/MuscleIq${muscle.image}`} 
              alt={muscle.label} 
              style={{ width: '100%', height: '100%', objectFit: 'cover' }} 
            />
            <div style={{
              position: 'absolute',
              bottom: 0, left: 0, right: 0, height: '50%',
              background: 'linear-gradient(to top, rgba(0,0,0,0.9), transparent)',
              pointerEvents: 'none'
            }}></div>
            <span style={{ 
              position: 'absolute', 
              bottom: '0.8rem', left: '0.8rem', 
              color: 'white', fontWeight: 'bold', fontSize: '1.1rem',
              textShadow: '1px 1px 2px rgba(0,0,0,0.8)'
            }}>
              {muscle.label}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
}
