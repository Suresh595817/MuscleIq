const express = require('express');
const cors = require('cors');

// Initialize Express App
const app = express();
const PORT = process.env.PORT || 5000;

// Middleware
app.use(cors());
app.use(express.json());

// Routes
const workoutRoutes = require('./routes/workoutRoutes');
const authRoutes = require('./routes/authRoutes');

// Mount Routes
app.use('/api/workouts', workoutRoutes);
app.use('/api/auth', authRoutes);

// Root Endpoint for testing
app.get('/', (req, res) => {
  res.send('MuscleIQ API is running!');
});

// Start Server
app.listen(PORT, '0.0.0.0', () => {
  console.log(`Server running on http://0.0.0.0:${PORT}`);
});
