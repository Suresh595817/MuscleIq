import React, { useState } from 'react';

import { Sparkles, Dumbbell } from 'lucide-react';

export default function AIWorkout() {
  const [time, setTime] = useState("30 mins");
  const [equipment, setEquipment] = useState("Dumbbells");
  const [focus, setFocus] = useState("Full Body");
  const [prompt, setPrompt] = useState("");
  
  const [loading, setLoading] = useState(false);
  const [workoutPlan, setWorkoutPlan] = useState("");
  const [error, setError] = useState("");

  const generateWorkout = async (e) => {
    e.preventDefault();
    
    setLoading(true);
    setError("");
    
    const finalPrompt = `Time available: ${time}\nEquipment available: ${equipment}\nFocus area: ${focus}\nCustom request: ${prompt}`;

    try {
      const response = await fetch("http://localhost:11434/api/generate", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          model: "llama3",
          prompt: `You are an AI Coach, an expert personal trainer. Create a highly structured workout routine based on this request:\n\n${finalPrompt}\n\nFormat it nicely with markdown, focusing on sets, reps, and target muscles. Keep it engaging.`,
          stream: false
        })
      });
      
      if (!response.ok) {
        throw new Error("Network response was not ok");
      }
      
      const data = await response.json();
      setWorkoutPlan(data.response);
    } catch (err) {
      console.error(err);
      setError("Failed to connect to local Ollama instance. Is Ollama running?");
    }
    setLoading(false);
  };

  return (
    <div className="animate-fade-in" style={{ maxWidth: '800px', margin: '0 auto' }}>
      <div style={{ textAlign: 'center', marginBottom: '3rem' }}>
        <Sparkles size={48} color="#8b5cf6" style={{ marginBottom: '1rem' }} />
        <h1 style={{ fontSize: '2.5rem', margin: '0 0 1rem 0' }}>AI Workout Generator</h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem' }}>
          Tell Ollama what you want to train today, and let local AI build the perfect routine.
        </p>
      </div>

      <div className="glass-panel" style={{ padding: '2rem', marginBottom: '2rem' }}>
        <form onSubmit={generateWorkout} style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-muted)' }}>Time Available</label>
            <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
              {["15 mins", "30 mins", "45 mins", "60 mins"].map(t => (
                <div 
                  key={t}
                  onClick={() => setTime(t)}
                  style={{
                    padding: '0.5rem 1rem',
                    borderRadius: '2rem',
                    background: time === t ? 'rgba(139, 92, 246, 0.2)' : 'rgba(255,255,255,0.05)',
                    border: `1px solid ${time === t ? '#8b5cf6' : 'transparent'}`,
                    color: time === t ? '#8b5cf6' : 'var(--text-muted)',
                    cursor: 'pointer',
                    transition: 'all 0.2s'
                  }}
                >
                  {t}
                </div>
              ))}
            </div>
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-muted)' }}>Equipment</label>
            <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
              {["Bodyweight", "Dumbbells", "Barbell", "Full Gym"].map(e => (
                <div 
                  key={e}
                  onClick={() => setEquipment(e)}
                  style={{
                    padding: '0.5rem 1rem',
                    borderRadius: '2rem',
                    background: equipment === e ? 'rgba(139, 92, 246, 0.2)' : 'rgba(255,255,255,0.05)',
                    border: `1px solid ${equipment === e ? '#8b5cf6' : 'transparent'}`,
                    color: equipment === e ? '#8b5cf6' : 'var(--text-muted)',
                    cursor: 'pointer',
                    transition: 'all 0.2s'
                  }}
                >
                  {e}
                </div>
              ))}
            </div>
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-muted)' }}>Focus Area</label>
            <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
              {["Full Body", "Upper Body", "Lower Body", "Core"].map(f => (
                <div 
                  key={f}
                  onClick={() => setFocus(f)}
                  style={{
                    padding: '0.5rem 1rem',
                    borderRadius: '2rem',
                    background: focus === f ? 'rgba(139, 92, 246, 0.2)' : 'rgba(255,255,255,0.05)',
                    border: `1px solid ${focus === f ? '#8b5cf6' : 'transparent'}`,
                    color: focus === f ? '#8b5cf6' : 'var(--text-muted)',
                    cursor: 'pointer',
                    transition: 'all 0.2s'
                  }}
                >
                  {f}
                </div>
              ))}
            </div>
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', color: 'var(--text-muted)' }}>Additional Requests (Optional)</label>
            <textarea 
              value={prompt}
              onChange={(e) => setPrompt(e.target.value)}
              placeholder="e.g. Include some mobility work at the end..."
              style={{ 
                width: '100%', 
                minHeight: '80px', 
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
          </div>

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
