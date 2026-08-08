const express = require('express');
const router = express.Router();
const { getUserWorkouts, createWorkout } = require('../controllers/workoutController');

// Routes mapping to Controller functions
router.get('/', getUserWorkouts);
router.post('/', createWorkout);

module.exports = router;
