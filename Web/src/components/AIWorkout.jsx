import React, { useState } from 'react';
import { generativeModel } from '../lib/gemini';
import { Sparkles, Dumbbell } from 'lucide-react';

export default function AIWorkout() {
  const [prompt, setPrompt] = useState("");
  const [loading, setLoading] = useState(false);
  const [workoutPlan, setWorkoutPlan] = useState("");
  const [error, setError] = useState("");

  const generateWorkout = async (e) => {
    e.preventDefault();
    if (!prompt.trim()) return;
    
    setLoading(true);
    setError("");
    try {
      const result = await generativeModel.generateContent(
        `You are MuscleIQ, an expert personal trainer. Create a highly structured workout routine based on this request: ${prompt}. 
        Format it nicely with markdown, focusing on sets, reps, and target muscles. Keep it engaging.`
      );
      setWorkoutPlan(result.response.text());
    } catch (err) {
      console.error(err);
      setError("Failed to generate workout. Check your Gemini API key in .env.");
    }
    setLoading(false);
  };

  return (
    <div className="animate-fade-in" style={{ maxWidth: '800px', margin: '0 auto' }}>
      <div style={{ textAlign: 'center', marginBottom: '3rem' }}>
        <Sparkles size={48} color="#8b5cf6" style={{ marginBottom: '1rem' }} />
        <h1 style={{ fontSize: '2.5rem', margin: '0 0 1rem 0' }}>AI Workout Generator</h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem' }}>
          Tell Gemini what you want to train today, and let it build the perfect routine.
        </p>
      </div>

      <div className="glass-panel" style={{ padding: '2rem', marginBottom: '2rem' }}>
        <form onSubmit={generateWorkout} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          <textarea 
            value={prompt}
            onChange={(e) => setPrompt(e.target.value)}
            placeholder="e.g. I have 45 minutes and want to do a heavy back and bicep workout focusing on hypertrophy..."
            style={{ 
              width: '100%', 
              minHeight: '120px', 
              background: 'rgba(0,0,0,0.2)', 
              border: '1px solid var(--card-border)', 
              borderRadius: '0.5rem',
              padding: '1rem',
              color: 'white',
              fontFamily: 'inherit',
              resize: 'vertical',
              outline: 'none'
            }}
          />
          <button 
            type="submit" 
            className="btn-primary" 
            disabled={loading}
            style={{ alignSelf: 'flex-end', display: 'flex', alignItems: 'center', gap: '0.5rem', opacity: loading ? 0.7 : 1 }}
          >
            {loading ? 'Generating...' : <><Sparkles size={18} /> Generate Plan</>}
          </button>
        </form>
      </div>

      {error && (
        <div className="glass-panel" style={{ padding: '1rem', color: '#ef4444', border: '1px solid rgba(239, 68, 68, 0.3)' }}>
          {error}
        </div>
      )}

      {workoutPlan && (
        <div className="glass-panel animate-fade-in" style={{ padding: '2rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1.5rem', borderBottom: '1px solid var(--card-border)', paddingBottom: '1rem' }}>
            <Dumbbell color="#8b5cf6" />
            <h2 style={{ margin: 0, fontSize: '1.25rem' }}>Your Custom Plan</h2>
          </div>
          <div style={{ whiteSpace: 'pre-wrap', lineHeight: '1.6', color: 'var(--text-main)' }}>
            {/* Very basic markdown rendering for the demo */}
            {workoutPlan.split('\n').map((line, i) => {
              if (line.startsWith('**') || line.startsWith('#')) {
                return <h3 key={i} style={{ color: '#8b5cf6', marginTop: '1.5rem', marginBottom: '0.5rem' }}>{line.replace(/[*#]/g, '')}</h3>
              }
              if (line.startsWith('*') || line.startsWith('-')) {
                return <div key={i} style={{ marginLeft: '1rem', marginBottom: '0.5rem' }}>• {line.substring(1).trim()}</div>
              }
              return <p key={i} style={{ margin: '0.5rem 0' }}>{line}</p>
            })}
          </div>
        </div>
      )}
    </div>
  );
}
