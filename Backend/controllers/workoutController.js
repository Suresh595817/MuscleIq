const { db } = require("../config/firebaseAdmin");

// @desc    Get workouts for a specific user
// @route   GET /api/workouts?userId=xxx
exports.getUserWorkouts = async (req, res) => {
  try {
    const { userId } = req.query;
    console.log("Fetching workouts for userId:", userId);
    
    if (!userId) {
      return res.status(400).json({ message: "User ID is required" });
    }

    const workoutsRef = db.collection("Workouts");
    const snapshot = await workoutsRef.where("userId", "==", userId).get();
    
    if (snapshot.empty) {
      return res.status(200).json([]);
    }

    const workouts = [];
    snapshot.forEach(doc => {
      const data = doc.data();
      if (data.date && data.date.toDate) {
        data.date = data.date.toDate().toISOString();
      }
      workouts.push({ id: doc.id, ...data });
    });

    workouts.sort((a, b) => {
      const dateA = new Date(a.date).getTime() || 0;
      const dateB = new Date(b.date).getTime() || 0;
      return dateB - dateA;
    });

    res.status(200).json(workouts);
  } catch (error) {
    console.error("Error fetching workouts:", error);
    res.status(500).json({ message: "Server Error", error: error.message });
  }
};

// @desc    Create a new workout
// @route   POST /api/workouts
exports.createWorkout = async (req, res) => {
  try {
    const workoutData = req.body;
    
    if (!workoutData || !workoutData.userId) {
      return res.status(400).json({ message: "Invalid workout data or missing userId" });
    }

    // Add server timestamp using admin SDK
    workoutData.date = new Date();

    const docRef = await db.collection("Workouts").add(workoutData);
    
    res.status(201).json({ id: docRef.id, message: "Workout created successfully" });
  } catch (error) {
    console.error("Error creating workout:", error);
    res.status(500).json({ message: "Server Error", error: error.message });
  }
};
