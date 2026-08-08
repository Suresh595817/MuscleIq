import React, { useState } from 'react';
import { ArrowLeft, CheckCircle, RefreshCw, AlertTriangle, Info } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import MuscleHeatmap from './MuscleHeatmap';

const MuscleStatus = {
  BALANCED: 'BALANCED',
  UNDERTRAINED: 'UNDERTRAINED',
  NEGLECTED: 'NEGLECTED',
  OVERTRAINED: 'OVERTRAINED',
  UNKNOWN: 'UNKNOWN'
};

const MuscleColors = {
  BALANCED: '#10B981', // MuscleGreen
  UNDERTRAINED: '#F59E0B', // MuscleYellow
  NEGLECTED: '#EF4444', // MuscleRed
  OVERTRAINED: '#8B5CF6', // MuscleOvertrained
  UNKNOWN: '#374151' // Dark300
};

export default function MuscleMap() {
  const navigate = useNavigate();
  const [view, setView] = useState("front");
  const [selectedMuscle, setSelectedMuscle] = useState(null);

  const getMuscleScore = (muscle) => {
    switch (muscle) {
      case "Chest":
      case "Front Delts":
        return { score: 85, status: MuscleStatus.BALANCED, message: "Perfectly balanced. Keep up the good work." };
      case "Lats":
      case "Upper Back":
        return { score: 40, status: MuscleStatus.UNDERTRAINED, message: "Slightly undertrained. Consider adding volume." };
      case "Hamstrings":
      case "Calves":
        return { score: 20, status: MuscleStatus.NEGLECTED, message: "Critically neglected. High risk of imbalance." };
      case "Quads":
      case "Biceps":
        return { score: 15, status: MuscleStatus.OVERTRAINED, message: "Overtrained. Needs recovery time." };
      default:
        return { score: 50, status: MuscleStatus.UNKNOWN, message: "Log more workouts to get insights." };
    }
  };

  const getMuscleColor = (muscle) => {
    const status = getMuscleScore(muscle).status;
    return MuscleColors[status];
  };

  const selectedData = selectedMuscle ? getMuscleScore(selectedMuscle) : null;

  const StatusIcon = ({ status, size, color }) => {
    switch (status) {
      case MuscleStatus.BALANCED: return <CheckCircle size={size} color={color} />;
      case MuscleStatus.UNDERTRAINED: return <RefreshCw size={size} color={color} />;
      case MuscleStatus.NEGLECTED:
      case MuscleStatus.OVERTRAINED: return <AlertTriangle size={size} color={color} />;
      default: return <Info size={size} color={color} />;
    }
  };

  return (
    <div className="animate-fade-in" style={{ maxWidth: '800px', margin: '0 auto', position: 'relative', height: '100%' }}>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '2rem' }}>
        <button onClick={() => navigate(-1)} style={{ background: 'transparent', border: 'none', color: 'white', cursor: 'pointer', display: 'flex', alignItems: 'center' }}>
          <ArrowLeft size={24} style={{ marginRight: '1rem' }} />
        </button>
        <h1 style={{ fontSize: '1.5rem', margin: 0, fontWeight: 'bold' }}>Muscle Imbalance Map</h1>
      </div>

      {/* Controls */}
      <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '2rem' }}>
        <div style={{ background: 'var(--card-bg)', border: '1px solid var(--card-border)', borderRadius: '2rem', padding: '0.2rem', display: 'flex' }}>
          <button 
            onClick={() => { setView('front'); setSelectedMuscle(null); }}
            style={{ 
              background: view === 'front' ? '#3B82F6' : 'transparent',
              color: 'white', border: 'none', padding: '0.5rem 1.5rem', borderRadius: '2rem', cursor: 'pointer', fontWeight: 'bold' 
            }}>
            Front
          </button>
          <button 
            onClick={() => { setView('back'); setSelectedMuscle(null); }}
            style={{ 
              background: view === 'back' ? '#3B82F6' : 'transparent',
              color: 'white', border: 'none', padding: '0.5rem 1.5rem', borderRadius: '2rem', cursor: 'pointer', fontWeight: 'bold' 
            }}>
            Back
          </button>
        </div>
      </div>

      {/* Legend */}
      <div style={{ display: 'flex', justifyContent: 'center', flexWrap: 'wrap', gap: '1rem', marginBottom: '2rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
          <div style={{ width: '12px', height: '12px', borderRadius: '50%', background: MuscleColors.BALANCED }}></div> Balanced
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
          <div style={{ width: '12px', height: '12px', borderRadius: '50%', background: MuscleColors.UNDERTRAINED }}></div> Undertrained
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
          <div style={{ width: '12px', height: '12px', borderRadius: '50%', background: MuscleColors.NEGLECTED }}></div> Neglected
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.3rem', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
          <div style={{ width: '12px', height: '12px', borderRadius: '50%', background: MuscleColors.OVERTRAINED }}></div> Overtrained
        </div>
      </div>

      {/* Heatmap */}
      <div style={{ height: '400px', display: 'flex', justifyContent: 'center', alignItems: 'center', marginBottom: '4rem' }}>
        <MuscleHeatmap 
          viewMode={view} 
          getMuscleColor={getMuscleColor} 
          onMuscleClick={(muscle) => setSelectedMuscle(muscle)} 
        />
      </div>

      {/* Detail Bottom Sheet */}
      {selectedMuscle && selectedData && (
        <div className="glass-panel animate-fade-in" style={{ 
          position: 'fixed', bottom: 0, left: 0, right: 0, 
          padding: '2rem', borderTopLeftRadius: '1.5rem', borderTopRightRadius: '1.5rem',
          borderBottom: 'none', zIndex: 100, border: '1px solid var(--card-border)', background: '#111827'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', marginBottom: '1.5rem' }}>
            <div style={{ 
              background: `${getMuscleColor(selectedMuscle)}20`, 
              padding: '1rem', borderRadius: '50%', marginRight: '1rem' 
            }}>
              <StatusIcon status={selectedData.status} size={32} color={getMuscleColor(selectedMuscle)} />
            </div>
            <div>
              <h2 style={{ margin: '0 0 0.2rem 0', fontSize: '1.5rem', fontWeight: 'bold' }}>{selectedMuscle}</h2>
              <div style={{ color: getMuscleColor(selectedMuscle), fontWeight: 'bold', fontSize: '1rem' }}>
                {selectedData.status}
              </div>
            </div>
          </div>
          <p style={{ color: 'var(--text-muted)', fontSize: '1rem', lineHeight: '1.5', marginBottom: '2rem' }}>
            {selectedData.message}
          </p>
          <button 
            onClick={() => setSelectedMuscle(null)} 
            style={{ 
              width: '100%', padding: '1rem', background: 'rgba(255,255,255,0.1)', color: 'white', 
              border: 'none', borderRadius: '1rem', fontSize: '1rem', fontWeight: 'bold', cursor: 'pointer' 
            }}>
            Close
          </button>
        </div>
      )}
    </div>
  );
}
