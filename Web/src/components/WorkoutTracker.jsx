import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { auth } from '../lib/firebase';
import { Plus, Save, Trash2, Dumbbell, Clock, Activity } from 'lucide-react';

export default function WorkoutTracker() {
  const location = useLocation();
  const initialMuscle = location.state?.selectedMuscle || "Chest";

  const [workoutName, setWorkoutName] = useState(location.state?.selectedMuscle ? `${location.state.selectedMuscle} Day` : "");
  const [duration, setDuration] = useState("");
  const [exercises, setExercises] = useState([
    { exerciseName: "", primaryMuscle: initialMuscle, sets: [] }
  ]);
  const [isSaving, setIsSaving] = useState(false);
  const [saveSuccess, setSaveSuccess] = useState(false);
  const [error, setError] = useState("");

  const addExercise = () => {
    setExercises([...exercises, { exerciseName: "", primaryMuscle: "Chest", sets: [] }]);
  };

  const removeExercise = (index) => {
    const updated = [...exercises];
    updated.splice(index, 1);
    setExercises(updated);
  };

  const updateExercise = (index, field, value) => {
    const updated = [...exercises];
    updated[index][field] = value;
    setExercises(updated);
  };

  const addSet = (exerciseIndex) => {
    const updated = [...exercises];
    updated[exerciseIndex].sets.push({ reps: 0, weight: 0 });
    setExercises(updated);
  };

  const removeSet = (exerciseIndex, setIndex) => {
    const updated = [...exercises];
    updated[exerciseIndex].sets.splice(setIndex, 1);
    setExercises(updated);
  };

  const updateSet = (exerciseIndex, setIndex, field, value) => {
    const updated = [...exercises];
    updated[exerciseIndex].sets[setIndex][field] = value;
    setExercises(updated);
  };

  const saveWorkout = async (e) => {
    e.preventDefault();
    if (!workoutName || exercises.length === 0) {
      setError("Please add a workout name and at least one exercise.");
      return;
    }
    
    setIsSaving(true);
    setError("");
    setSaveSuccess(false);

    try {
      const workoutData = {
        userId: auth.currentUser?.uid || "guest_web_user",
        name: workoutName,
        durationMinutes: parseInt(duration) || 0,
        date: new Date(),
        exercises: exercises.map(ex => ({
          exerciseName: ex.exerciseName || "Unnamed Exercise",
          primaryMuscle: ex.primaryMuscle,
          sets: ex.sets.map(s => ({
            reps: parseInt(s.reps) || 0,
            weight: parseFloat(s.weight) || 0
          }))
        }))
      };

      const response = await fetch('http://localhost:5000/api/workouts', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(workoutData)
      });

      if (!response.ok) {
        throw new Error('Network response was not ok');
      }

      setSaveSuccess(true);
      
      // Reset form
      setWorkoutName("");
      setDuration("");
      setExercises([]);
      
      setTimeout(() => setSaveSuccess(false), 3000);
    } catch (err) {
      console.error("Error saving workout: ", err);
      setError("Failed to save workout. Please try again.");
    }
    setIsSaving(false);
  };

  return (
    <div className="animate-fade-in" style={{ maxWidth: '800px', margin: '0 auto' }}>
      <div style={{ textAlign: 'center', marginBottom: '3rem' }}>
        <Activity size={48} color="#8b5cf6" style={{ marginBottom: '1rem' }} />
        <h1 style={{ fontSize: '2.5rem', margin: '0 0 1rem 0' }}>Workout Tracker</h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem' }}>
          Log your exercises, sets, and weights in real-time.
        </p>
      </div>

      {error && (
        <div className="glass-panel" style={{ padding: '1rem', color: '#ef4444', border: '1px solid rgba(239, 68, 68, 0.3)', marginBottom: '1.5rem' }}>
          {error}
        </div>
      )}

      {saveSuccess && (
        <div className="glass-panel" style={{ padding: '1rem', color: '#10b981', border: '1px solid rgba(16, 185, 129, 0.3)', marginBottom: '1.5rem', textAlign: 'center', fontWeight: 'bold' }}>
          Workout saved successfully! It will now appear in your Analytics.
        </div>
      )}

      <form onSubmit={saveWorkout}>
        <div className="glass-panel" style={{ padding: '2rem', marginBottom: '2rem' }}>
          <h2 style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginTop: 0, marginBottom: '1.5rem' }}>
            <Dumbbell color="#8b5cf6" /> Workout Details
          </h2>
          <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
            <div style={{ flex: '1 1 300px' }}>
              <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-muted)' }}>Workout Name</label>
              <input
                type="text"
                value={workoutName}
                onChange={(e) => setWorkoutName(e.target.value)}
                placeholder="e.g. Chest & Triceps"
                style={{ width: '100%', padding: '0.75rem', borderRadius: '0.5rem', border: '1px solid var(--card-border)', background: 'rgba(0,0,0,0.5)', color: 'white', outline: 'none', fontSize: '1.2rem', fontWeight: 'bold' }}
              />
            </div>
            <div style={{ flex: '1 1 150px' }}>
              <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-muted)' }}>Duration (min)</label>
              <input
                type="number"
                value={duration}
                onChange={(e) => setDuration(e.target.value)}
                placeholder="e.g. 60"
                style={{ width: '100%', padding: '0.75rem', borderRadius: '0.5rem', border: '1px solid var(--card-border)', background: 'rgba(0,0,0,0.5)', color: 'white', outline: 'none', fontSize: '1.2rem', fontWeight: 'bold' }}
              />
            </div>
          </div>
        </div>

        {exercises.map((exercise, exIdx) => (
          <div key={exIdx} className="glass-panel animate-fade-in" style={{ padding: '2rem', marginBottom: '2rem', borderLeft: '4px solid #8b5cf6' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
              <div style={{ display: 'flex', gap: '1rem', flex: 1 }}>
                <input
                  type="text"
                  placeholder="Exercise Name"
                  value={exercise.exerciseName}
                  onChange={(e) => updateExercise(exIdx, 'exerciseName', e.target.value)}
                  style={{ flex: 2, padding: '0.75rem', borderRadius: '0.5rem', border: '1px solid var(--card-border)', background: 'rgba(0,0,0,0.5)', color: 'white', outline: 'none', fontSize: '1.1rem', fontWeight: 'bold' }}
                />
                <select
                  value={exercise.primaryMuscle}
                  onChange={(e) => updateExercise(exIdx, 'primaryMuscle', e.target.value)}
                  style={{ flex: 1, padding: '0.75rem', borderRadius: '0.5rem', border: '1px solid var(--card-border)', background: 'rgba(20,20,20,0.9)', color: 'white', outline: 'none' }}
                >
                  <option value="Chest">Chest</option>
                  <option value="Front Delts">Front Delts</option>
                  <option value="Rear Delts">Rear Delts</option>
                  <option value="Upper Back">Upper Back</option>
                  <option value="Lats">Lats</option>
                  <option value="Biceps">Biceps</option>
                  <option value="Triceps">Triceps</option>
                  <option value="Forearms">Forearms</option>
                  <option value="Abs">Abs</option>
                  <option value="Obliques">Obliques</option>
                  <option value="Quads">Quads</option>
                  <option value="Hamstrings">Hamstrings</option>
                  <option value="Glutes">Glutes</option>
                  <option value="Calves">Calves</option>
                </select>
              </div>
              <button type="button" onClick={() => removeExercise(exIdx)} style={{ background: 'transparent', border: 'none', color: '#ef4444', cursor: 'pointer', padding: '0.5rem', marginLeft: '1rem' }}>
                <Trash2 size={20} />
              </button>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', marginBottom: '1rem' }}>
              {exercise.sets.map((set, setIdx) => (
                <div key={setIdx} className="animate-fade-in" style={{ display: 'flex', gap: '1rem', alignItems: 'center', background: 'rgba(255,255,255,0.03)', padding: '0.75rem', borderRadius: '0.5rem' }}>
                  <span style={{ color: 'var(--text-muted)', width: '2rem', textAlign: 'center', fontWeight: 'bold' }}>{setIdx + 1}</span>
                  <div style={{ flex: 1, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <input
                      type="number"
                      placeholder="kg"
                      value={set.weight || ""}
                      onChange={(e) => updateSet(exIdx, setIdx, 'weight', e.target.value)}
                      style={{ width: '80px', padding: '0.5rem', borderRadius: '0.5rem', border: '1px solid var(--card-border)', background: 'rgba(0,0,0,0.5)', color: 'white', textAlign: 'center', outline: 'none', fontWeight: '600' }}
                    />
                    <span style={{ color: 'var(--text-muted)' }}>kg</span>
                  </div>
                  <div style={{ flex: 1, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <input
                      type="number"
                      placeholder="reps"
                      value={set.reps || ""}
                      onChange={(e) => updateSet(exIdx, setIdx, 'reps', e.target.value)}
                      style={{ width: '80px', padding: '0.5rem', borderRadius: '0.5rem', border: '1px solid var(--card-border)', background: 'rgba(0,0,0,0.5)', color: 'white', textAlign: 'center', outline: 'none', fontWeight: '600' }}
                    />
                    <span style={{ color: 'var(--text-muted)' }}>reps</span>
                  </div>
                  <button type="button" onClick={() => removeSet(exIdx, setIdx)} style={{ background: 'transparent', border: 'none', color: '#ef4444', cursor: 'pointer', padding: '0.5rem' }}>
                    <Trash2 size={16} />
                  </button>
                </div>
              ))}
            </div>

            <button
              type="button"
              onClick={() => addSet(exIdx)}
              style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', background: 'rgba(255,255,255,0.05)', color: 'white', border: '1px dashed var(--card-border)', padding: '0.5rem 1rem', borderRadius: '0.5rem', cursor: 'pointer', width: '100%', justifyContent: 'center' }}
            >
              <Plus size={16} /> Add Set
            </button>
          </div>
        ))}

        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '2rem' }}>
          <button
            type="button"
            onClick={addExercise}
            style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', background: 'rgba(139, 92, 246, 0.2)', color: '#a78bfa', border: '1px solid rgba(139, 92, 246, 0.5)', padding: '0.75rem 1.5rem', borderRadius: '0.5rem', cursor: 'pointer', fontWeight: 'bold' }}
          >
            <Plus size={18} /> Add Exercise
          </button>
          
          <button 
            type="submit" 
            className="btn-primary" 
            disabled={isSaving}
            style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', opacity: isSaving ? 0.7 : 1 }}
          >
            <Save size={18} /> {isSaving ? 'Saving...' : 'Complete Workout'}
          </button>
        </div>
      </form>
    </div>
  );
}
