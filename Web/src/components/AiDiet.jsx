import React, { useState } from 'react';

function AiDiet() {
  const [goal, setGoal] = useState('Cut fat');
  const [weight, setWeight] = useState('80');
  const [preference, setPreference] = useState('No restrictions');
  
  const [loading, setLoading] = useState(false);
  const [dietPlan, setDietPlan] = useState(null);
  const [error, setError] = useState(null);

  const generateDiet = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setDietPlan(null);

    const prompt = `
      You are an AI Coach, an expert sports nutritionist. 
      Create a personalized daily diet plan for a person who weighs ${weight} kg, wants to ${goal}, and has a ${preference} dietary preference.
      
      Return ONLY valid JSON in this exact format, with NO markdown formatting, NO backticks, and NO other text before or after:
      {
          "macros": {
              "calories": 2500,
              "protein": 180,
              "carbs": 250,
              "fats": 70
          },
          "meals": [
              {
                  "name": "Breakfast",
                  "items": ["3 Scrambled Eggs", "2 slices whole wheat toast", "1 cup blueberries"]
              },
              {
                  "name": "Lunch",
                  "items": ["200g Grilled Chicken Breast", "150g Quinoa", "Broccoli"]
              }
          ]
      }
    `;

    try {
      const response = await fetch('http://localhost:11434/api/generate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          model: 'llama3',
          prompt: prompt,
          stream: false,
        }),
      });

      if (!response.ok) {
        throw new Error('Failed to connect to local Ollama instance. Is Ollama running?');
      }

      const data = await response.json();
      
      // Clean up potential markdown formatting from Ollama
      let cleanJsonString = data.response.replace(/```json/g, '').replace(/```/g, '').trim();
      
      const parsedDiet = JSON.parse(cleanJsonString);
      setDietPlan(parsedDiet);
    } catch (err) {
      console.error(err);
      setError(err.message || 'An error occurred while generating the diet plan.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container" style={{ padding: '2rem', maxWidth: '800px', margin: '0 auto' }}>
      <div className="card" style={{ marginBottom: '2rem' }}>
        <h2>AI Diet Plan</h2>
        <p style={{ color: '#9CA3AF', marginBottom: '1.5rem' }}>
          Generate a personalized, macro-calculated diet plan instantly using AI.
        </p>

        <form onSubmit={generateDiet}>
          <div style={{ marginBottom: '1rem' }}>
            <label style={{ display: 'block', marginBottom: '0.5rem', color: '#E5E7EB' }}>Your Goal</label>
            <input 
              type="text" 
              className="input-field"
              value={goal}
              onChange={(e) => setGoal(e.target.value)}
              placeholder="e.g. Bulk, Cut, Maintain"
              required
            />
          </div>

          <div style={{ marginBottom: '1rem' }}>
            <label style={{ display: 'block', marginBottom: '0.5rem', color: '#E5E7EB' }}>Body Weight (kg)</label>
            <input 
              type="number" 
              className="input-field"
              value={weight}
              onChange={(e) => setWeight(e.target.value)}
              required
            />
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ display: 'block', marginBottom: '0.5rem', color: '#E5E7EB' }}>Dietary Preference</label>
            <input 
              type="text" 
              className="input-field"
              value={preference}
              onChange={(e) => setPreference(e.target.value)}
              placeholder="e.g. Vegetarian, Keto, None"
              required
            />
          </div>

          <button 
            type="submit" 
            className="btn btn-primary" 
            style={{ width: '100%' }}
            disabled={loading}
          >
            {loading ? 'Consulting AI Coach...' : 'Generate Custom Diet Plan'}
          </button>
        </form>

        {error && (
          <div style={{ marginTop: '1rem', padding: '1rem', backgroundColor: 'rgba(239, 68, 68, 0.1)', color: '#EF4444', borderRadius: '0.5rem' }}>
            {error}
          </div>
        )}
      </div>

      {dietPlan && (
        <div className="diet-results">
          <div className="card" style={{ backgroundColor: 'rgba(16, 185, 129, 0.1)', border: '1px solid #10B981', marginBottom: '1.5rem' }}>
            <h3 style={{ color: '#10B981', marginBottom: '1rem' }}>Daily Targets</h3>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div><strong>Calories:</strong> {dietPlan.macros.calories} kcal</div>
              <div><strong>Protein:</strong> {dietPlan.macros.protein}g</div>
              <div><strong>Carbs:</strong> {dietPlan.macros.carbs}g</div>
              <div><strong>Fats:</strong> {dietPlan.macros.fats}g</div>
            </div>
          </div>

          <h3 style={{ marginBottom: '1rem' }}>Your Menu</h3>
          {dietPlan.meals.map((meal, index) => (
            <div key={index} className="card" style={{ marginBottom: '1rem' }}>
              <h4 style={{ color: '#E5E7EB', marginBottom: '0.5rem' }}>{meal.name}</h4>
              <ul style={{ paddingLeft: '1.5rem', color: '#9CA3AF' }}>
                {meal.items.map((item, i) => (
                  <li key={i} style={{ marginBottom: '0.25rem' }}>{item}</li>
                ))}
              </ul>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default AiDiet;
