# 🎓 School Management System - Frontend Integration Guide

## 📋 Overview

This document provides comprehensive guidance for creating React frontend components that integrate with the three newly implemented systems:

1. **🎓 Academic Tutoring System** - Personalized learning paths and AI tutoring
2. **🎮 Gamification System** - Points, badges, and leaderboards
3. **👥 Peer Learning Platform** - Study groups and collaborative learning

## 🔐 Authentication Setup

### Base Configuration
```javascript
// config/api.js
const API_BASE_URL = 'http://localhost:9091/api';

// Authentication helper
export const authAPI = {
  login: async (usernameOrEmail, password) => {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        usernameOrEmail,
        password
      })
    });
    
    const data = await response.json();
    if (response.ok) {
      localStorage.setItem('jwtToken', data.data.jwtToken);
      return data.data;
    }
    throw new Error(data.message || 'Login failed');
  },

  getAuthHeaders: () => {
    const token = localStorage.getItem('jwtToken');
    return {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    };
  }
};
```

---

## 🎓 Academic Tutoring System

### 1. Dashboard Analytics Component

```jsx
// components/tutoring/DashboardAnalytics.jsx
import React, { useState, useEffect } from 'react';
import { authAPI } from '../../config/api';

const DashboardAnalytics = () => {
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAnalytics();
  }, []);

  const fetchAnalytics = async () => {
    try {
      const response = await fetch('http://localhost:9091/api/v1/tutoring/dashboard/analytics', {
        headers: authAPI.getAuthHeaders()
      });
      
      if (response.ok) {
        const data = await response.json();
        setAnalytics(data);
      }
    } catch (error) {
      console.error('Error fetching analytics:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div>Loading analytics...</div>;
  if (!analytics) return <div>No analytics data available</div>;

  return (
    <div className="dashboard-analytics">
      <h2>📊 Tutoring Dashboard Analytics</h2>
      
      {/* Key Metrics Cards */}
      <div className="metrics-grid">
        <div className="metric-card">
          <h3>Total Sessions</h3>
          <p className="metric-value">{analytics.totalSessions}</p>
        </div>
        
        <div className="metric-card">
          <h3>Completed Sessions</h3>
          <p className="metric-value">{analytics.completedSessions}</p>
        </div>
        
        <div className="metric-card">
          <h3>Learning Paths</h3>
          <p className="metric-value">{analytics.totalLearningPaths}</p>
        </div>
        
        <div className="metric-card">
          <h3>Learning Modules</h3>
          <p className="metric-value">{analytics.totalModules}</p>
        </div>
      </div>

      {/* Completion Rates */}
      <div className="completion-rates">
        <h3>📈 Completion Rates</h3>
        <div className="rate-item">
          <span>Sessions: {analytics.sessionCompletionRate}%</span>
          <div className="progress-bar">
            <div 
              className="progress-fill" 
              style={{width: `${analytics.sessionCompletionRate}%`}}
            ></div>
          </div>
        </div>
        
        <div className="rate-item">
          <span>Learning Paths: {analytics.learningPathCompletionRate}%</span>
          <div className="progress-bar">
            <div 
              className="progress-fill" 
              style={{width: `${analytics.learningPathCompletionRate}%`}}
            ></div>
          </div>
        </div>
        
        <div className="rate-item">
          <span>Modules: {analytics.moduleCompletionRate}%</span>
          <div className="progress-bar">
            <div 
              className="progress-fill" 
              style={{width: `${analytics.moduleCompletionRate}%`}}
            ></div>
          </div>
        </div>
      </div>

      {/* Subject Distribution */}
      <div className="subject-distribution">
        <h3>📚 Sessions by Subject</h3>
        <div className="subject-chart">
          {Object.entries(analytics.sessionsBySubject).map(([subject, count]) => (
            <div key={subject} className="subject-item">
              <span>{subject}: {count}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default DashboardAnalytics;
```

### 2. Tutoring Session Management

```jsx
// components/tutoring/TutoringSessionManager.jsx
import React, { useState, useEffect } from 'react';

const TutoringSessionManager = () => {
  const [sessions, setSessions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);

  useEffect(() => {
    fetchSessions();
  }, [page, size]);

  const fetchSessions = async () => {
    try {
      const response = await fetch(
        `http://localhost:9091/api/v1/tutoring/sessions?page=${page}&size=${size}`,
        { headers: authAPI.getAuthHeaders() }
      );
      
      if (response.ok) {
        const data = await response.json();
        setSessions(data.content || []);
      }
    } catch (error) {
      console.error('Error fetching sessions:', error);
    } finally {
      setLoading(false);
    }
  };

  const createSession = async (sessionData) => {
    try {
      const response = await fetch('http://localhost:9091/api/v1/tutoring/sessions', {
        method: 'POST',
        headers: authAPI.getAuthHeaders(),
        body: JSON.stringify(sessionData)
      });
      
      if (response.ok) {
        fetchSessions(); // Refresh the list
        return await response.json();
      }
    } catch (error) {
      console.error('Error creating session:', error);
    }
  };

  if (loading) return <div>Loading sessions...</div>;

  return (
    <div className="tutoring-session-manager">
      <h2>🎓 Tutoring Session Management</h2>
      
      {/* Create Session Form */}
      <div className="create-session-form">
        <h3>Create New Session</h3>
        <form onSubmit={async (e) => {
          e.preventDefault();
          const formData = new FormData(e.target);
          const sessionData = {
            studentId: parseInt(formData.get('studentId')),
            subject: formData.get('subject'),
            topic: formData.get('topic'),
            gradeLevel: formData.get('gradeLevel'),
            question: formData.get('question'),
            difficultyLevel: formData.get('difficultyLevel'),
            sessionStatus: 'ACTIVE',
            timeSpentMinutes: parseInt(formData.get('timeSpentMinutes')),
            studentSatisfactionRating: parseInt(formData.get('satisfactionRating')),
            comprehensionScore: parseFloat(formData.get('comprehensionScore')),
            followUpRequired: formData.get('followUpRequired') === 'on',
            teacherReviewRequired: formData.get('teacherReviewRequired') === 'on',
            notes: formData.get('notes'),
            costUsd: parseFloat(formData.get('costUsd')),
            aiProvider: formData.get('aiProvider'),
            aiResponse: formData.get('aiResponse')
          };
          
          await createSession(sessionData);
          e.target.reset();
        }}>
          <input name="studentId" type="number" placeholder="Student ID" required />
          <input name="subject" type="text" placeholder="Subject" required />
          <input name="topic" type="text" placeholder="Topic" required />
          <input name="gradeLevel" type="text" placeholder="Grade Level" required />
          <textarea name="question" placeholder="Student Question" required />
          <select name="difficultyLevel" required>
            <option value="">Select Difficulty</option>
            <option value="BEGINNER">Beginner</option>
            <option value="INTERMEDIATE">Intermediate</option>
            <option value="ADVANCED">Advanced</option>
            <option value="EXPERT">Expert</option>
          </select>
          <input name="timeSpentMinutes" type="number" placeholder="Time Spent (minutes)" />
          <input name="satisfactionRating" type="number" min="1" max="5" placeholder="Satisfaction Rating (1-5)" />
          <input name="comprehensionScore" type="number" min="0" max="100" placeholder="Comprehension Score (0-100)" />
          <label>
            <input name="followUpRequired" type="checkbox" />
            Follow-up Required
          </label>
          <label>
            <input name="teacherReviewRequired" type="checkbox" />
            Teacher Review Required
          </label>
          <textarea name="notes" placeholder="Additional Notes" />
          <input name="costUsd" type="number" step="0.01" placeholder="Cost (USD)" />
          <select name="aiProvider">
            <option value="">Select AI Provider</option>
            <option value="ChatGPT">ChatGPT</option>
            <option value="Claude">Claude</option>
            <option value="Gemini">Gemini</option>
          </select>
          <textarea name="aiResponse" placeholder="AI Response" />
          <button type="submit">Create Session</button>
        </form>
      </div>

      {/* Sessions List */}
      <div className="sessions-list">
        <h3>Recent Sessions</h3>
        <div className="sessions-grid">
          {sessions.map((session) => (
            <div key={session.id} className="session-card">
              <h4>{session.topic}</h4>
              <p><strong>Student:</strong> {session.studentName}</p>
              <p><strong>Subject:</strong> {session.subject}</p>
              <p><strong>Grade:</strong> {session.gradeLevel}</p>
              <p><strong>Status:</strong> {session.sessionStatus}</p>
              <p><strong>Satisfaction:</strong> {session.studentSatisfactionRating}/5</p>
              <p><strong>Comprehension:</strong> {session.comprehensionScore}%</p>
              <p><strong>Time Spent:</strong> {session.timeSpentMinutes} minutes</p>
              <div className="session-actions">
                <button onClick={() => {/* View details */}}>View Details</button>
                <button onClick={() => {/* Edit session */}}>Edit</button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default TutoringSessionManager;
```

### 3. Learning Path Management

```jsx
// components/tutoring/LearningPathManager.jsx
import React, { useState, useEffect } from 'react';

const LearningPathManager = () => {
  const [learningPaths, setLearningPaths] = useState([]);
  const [modules, setModules] = useState([]);
  const [selectedPath, setSelectedPath] = useState(null);

  useEffect(() => {
    fetchLearningPaths();
  }, []);

  const fetchLearningPaths = async () => {
    try {
      const response = await fetch(
        'http://localhost:9091/api/v1/tutoring/learning-paths?page=0&size=10',
        { headers: authAPI.getAuthHeaders() }
      );
      
      if (response.ok) {
        const data = await response.json();
        setLearningPaths(data.content || []);
      }
    } catch (error) {
      console.error('Error fetching learning paths:', error);
    }
  };

  const fetchModules = async (pathId) => {
    try {
      const response = await fetch(
        `http://localhost:9091/api/v1/tutoring/learning-modules?learningPathId=${pathId}`,
        { headers: authAPI.getAuthHeaders() }
      );
      
      if (response.ok) {
        const data = await response.json();
        setModules(data);
        setSelectedPath(pathId);
      }
    } catch (error) {
      console.error('Error fetching modules:', error);
    }
  };

  const createLearningPath = async (pathData) => {
    try {
      const response = await fetch('http://localhost:9091/api/v1/tutoring/learning-paths', {
        method: 'POST',
        headers: authAPI.getAuthHeaders(),
        body: JSON.stringify(pathData)
      });
      
      if (response.ok) {
        fetchLearningPaths(); // Refresh the list
        return await response.json();
      }
    } catch (error) {
      console.error('Error creating learning path:', error);
    }
  };

  const createLearningModule = async (moduleData) => {
    try {
      const response = await fetch('http://localhost:9091/api/v1/tutoring/learning-modules', {
        method: 'POST',
        headers: authAPI.getAuthHeaders(),
        body: JSON.stringify({
          ...moduleData,
          learningPathId: selectedPath
        })
      });
      
      if (response.ok) {
        fetchModules(selectedPath); // Refresh modules
        return await response.json();
      }
    } catch (error) {
      console.error('Error creating learning module:', error);
    }
  };

  return (
    <div className="learning-path-manager">
      <h2>📚 Learning Path Management</h2>
      
      <div className="learning-paths-section">
        <h3>Learning Paths</h3>
        <div className="paths-grid">
          {learningPaths.map((path) => (
            <div 
              key={path.id} 
              className={`path-card ${selectedPath === path.id ? 'selected' : ''}`}
              onClick={() => fetchModules(path.id)}
            >
              <h4>{path.pathName}</h4>
              <p><strong>Subject:</strong> {path.subject}</p>
              <p><strong>Grade:</strong> {path.gradeLevel}</p>
              <p><strong>Progress:</strong> {path.progressPercentage}%</p>
              <p><strong>Status:</strong> {path.pathStatus}</p>
              <div className="progress-bar">
                <div 
                  className="progress-fill" 
                  style={{width: `${path.progressPercentage}%`}}
                ></div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {selectedPath && (
        <div className="modules-section">
          <h3>Learning Modules</h3>
          <div className="modules-grid">
            {modules.map((module) => (
              <div key={module.id} className="module-card">
                <h4>{module.moduleName}</h4>
                <p><strong>Type:</strong> {module.moduleType}</p>
                <p><strong>Difficulty:</strong> {module.difficultyLevel}</p>
                <p><strong>Duration:</strong> {module.formattedEstimatedDuration}</p>
                <p><strong>Completed:</strong> {module.isCompleted ? 'Yes' : 'No'}</p>
                {module.isCompleted && (
                  <p><strong>Score:</strong> {module.scorePercentage}%</p>
                )}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default LearningPathManager;
```

---

## 🎮 Gamification System

### 1. Leaderboard Component

```jsx
// components/gamification/Leaderboard.jsx
import React, { useState, useEffect } from 'react';

const Leaderboard = () => {
  const [leaderboard, setLeaderboard] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchLeaderboard();
  }, []);

  const fetchLeaderboard = async () => {
    try {
      const response = await fetch(
        'http://localhost:9091/api/v1/gamification/leaderboard?limit=10',
        { headers: authAPI.getAuthHeaders() }
      );
      
      if (response.ok) {
        const data = await response.json();
        setLeaderboard(data);
      }
    } catch (error) {
      console.error('Error fetching leaderboard:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div>Loading leaderboard...</div>;

  return (
    <div className="leaderboard">
      <h2>🏆 Student Leaderboard</h2>
      
      <div className="leaderboard-list">
        {leaderboard.map((student, index) => (
          <div key={student.studentId} className={`leaderboard-item rank-${index + 1}`}>
            <div className="rank">
              {index === 0 && '🥇'}
              {index === 1 && '🥈'}
              {index === 2 && '🥉'}
              {index > 2 && `#${index + 1}`}
            </div>
            
            <div className="student-info">
              <h4>{student.studentName}</h4>
              <p>ID: {student.studentId}</p>
            </div>
            
            <div className="stats">
              <div className="stat">
                <span className="stat-label">Total Points:</span>
                <span className="stat-value">{student.totalPoints}</span>
              </div>
              <div className="stat">
                <span className="stat-label">Achievements:</span>
                <span className="stat-value">{student.achievementsCount}</span>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Leaderboard;
```

### 2. Achievement Management

```jsx
// components/gamification/AchievementManager.jsx
import React, { useState, useEffect } from 'react';

const AchievementManager = () => {
  const [achievements, setAchievements] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAchievements();
  }, []);

  const fetchAchievements = async () => {
    try {
      const response = await fetch(
        'http://localhost:9091/api/v1/gamification/achievements?page=0&size=10',
        { headers: authAPI.getAuthHeaders() }
      );
      
      if (response.ok) {
        const data = await response.json();
        setAchievements(data.content || []);
      }
    } catch (error) {
      console.error('Error fetching achievements:', error);
    } finally {
      setLoading(false);
    }
  };

  const createAchievement = async (achievementData) => {
    try {
      const response = await fetch('http://localhost:9091/api/v1/gamification/achievements', {
        method: 'POST',
        headers: authAPI.getAuthHeaders(),
        body: JSON.stringify(achievementData)
      });
      
      if (response.ok) {
        fetchAchievements(); // Refresh the list
        return await response.json();
      }
    } catch (error) {
      console.error('Error creating achievement:', error);
    }
  };

  if (loading) return <div>Loading achievements...</div>;

  return (
    <div className="achievement-manager">
      <h2>🎖️ Achievement Management</h2>
      
      {/* Create Achievement Form */}
      <div className="create-achievement-form">
        <h3>Create New Achievement</h3>
        <form onSubmit={async (e) => {
          e.preventDefault();
          const formData = new FormData(e.target);
          const achievementData = {
            achievementName: formData.get('achievementName'),
            description: formData.get('description'),
            category: formData.get('category'),
            difficultyLevel: formData.get('difficultyLevel'),
            rarity: formData.get('rarity'),
            achievementType: formData.get('achievementType'),
            pointsValue: parseInt(formData.get('pointsValue')),
            xpValue: parseInt(formData.get('xpValue')),
            badgeIcon: formData.get('badgeIcon'),
            badgeColor: formData.get('badgeColor'),
            rarityColor: formData.get('rarityColor'),
            difficultyIcon: formData.get('difficultyIcon'),
            categoryIcon: formData.get('categoryIcon'),
            isRepeatable: formData.get('isRepeatable') === 'on',
            isSecret: formData.get('isSecret') === 'on',
            criteria: formData.get('criteria'),
            targetCount: parseInt(formData.get('targetCount')),
            displayOrder: parseInt(formData.get('displayOrder'))
          };
          
          await createAchievement(achievementData);
          e.target.reset();
        }}>
          <input name="achievementName" type="text" placeholder="Achievement Name" required />
          <textarea name="description" placeholder="Description" required />
          
          <select name="category" required>
            <option value="">Select Category</option>
            <option value="ACADEMIC">Academic</option>
            <option value="SOCIAL">Social</option>
            <option value="CREATIVE">Creative</option>
            <option value="PHYSICAL">Physical</option>
            <option value="LEADERSHIP">Leadership</option>
          </select>
          
          <select name="difficultyLevel" required>
            <option value="">Select Difficulty</option>
            <option value="EASY">Easy</option>
            <option value="MEDIUM">Medium</option>
            <option value="HARD">Hard</option>
            <option value="EXPERT">Expert</option>
          </select>
          
          <select name="rarity" required>
            <option value="">Select Rarity</option>
            <option value="COMMON">Common</option>
            <option value="UNCOMMON">Uncommon</option>
            <option value="RARE">Rare</option>
            <option value="EPIC">Epic</option>
            <option value="LEGENDARY">Legendary</option>
          </select>
          
          <select name="achievementType" required>
            <option value="">Select Type</option>
            <option value="MILESTONE">Milestone</option>
            <option value="STREAK">Streak</option>
            <option value="CHALLENGE">Challenge</option>
            <option value="DISCOVERY">Discovery</option>
          </select>
          
          <input name="pointsValue" type="number" placeholder="Points Value" required />
          <input name="xpValue" type="number" placeholder="XP Value" required />
          <input name="badgeIcon" type="text" placeholder="Badge Icon (emoji)" />
          <input name="badgeColor" type="color" placeholder="Badge Color" />
          <input name="rarityColor" type="color" placeholder="Rarity Color" />
          <input name="difficultyIcon" type="text" placeholder="Difficulty Icon" />
          <input name="categoryIcon" type="text" placeholder="Category Icon" />
          
          <label>
            <input name="isRepeatable" type="checkbox" />
            Repeatable
          </label>
          
          <label>
            <input name="isSecret" type="checkbox" />
            Secret Achievement
          </label>
          
          <textarea name="criteria" placeholder="Achievement Criteria" />
          <input name="targetCount" type="number" placeholder="Target Count" />
          <input name="displayOrder" type="number" placeholder="Display Order" />
          
          <button type="submit">Create Achievement</button>
        </form>
      </div>

      {/* Achievements Grid */}
      <div className="achievements-grid">
        <h3>Available Achievements</h3>
        <div className="achievements-list">
          {achievements.map((achievement) => (
            <div key={achievement.id} className="achievement-card">
              <div className="achievement-header">
                <span className="achievement-icon">{achievement.badgeIcon}</span>
                <h4>{achievement.achievementName}</h4>
                <span className={`rarity-badge rarity-${achievement.rarity.toLowerCase()}`}>
                  {achievement.rarity}
                </span>
              </div>
              
              <p className="achievement-description">{achievement.description}</p>
              
              <div className="achievement-stats">
                <div className="stat">
                  <span>Points: {achievement.pointsValue}</span>
                </div>
                <div className="stat">
                  <span>XP: {achievement.xpValue}</span>
                </div>
                <div className="stat">
                  <span>Category: {achievement.category}</span>
                </div>
                <div className="stat">
                  <span>Difficulty: {achievement.difficultyLevel}</span>
                </div>
              </div>
              
              <div className="achievement-flags">
                {achievement.isRepeatable && <span className="flag repeatable">Repeatable</span>}
                {achievement.isSecret && <span className="flag secret">Secret</span>}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default AchievementManager;
```

---

## 👥 Peer Learning Platform

### 1. Study Group Management

```jsx
// components/peer-learning/StudyGroupManager.jsx
import React, { useState, useEffect } from 'react';

const StudyGroupManager = () => {
  const [studyGroups, setStudyGroups] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStudyGroups();
  }, []);

  const fetchStudyGroups = async () => {
    try {
      const response = await fetch(
        'http://localhost:9091/api/v1/peer-learning/study-groups?page=0&size=10',
        { headers: authAPI.getAuthHeaders() }
      );
      
      if (response.ok) {
        const data = await response.json();
        setStudyGroups(data.content || []);
      }
    } catch (error) {
      console.error('Error fetching study groups:', error);
    } finally {
      setLoading(false);
    }
  };

  const createStudyGroup = async (groupData) => {
    try {
      const response = await fetch('http://localhost:9091/api/v1/peer-learning/study-groups', {
        method: 'POST',
        headers: authAPI.getAuthHeaders(),
        body: JSON.stringify(groupData)
      });
      
      if (response.ok) {
        fetchStudyGroups(); // Refresh the list
        return await response.json();
      }
    } catch (error) {
      console.error('Error creating study group:', error);
    }
  };

  if (loading) return <div>Loading study groups...</div>;

  return (
    <div className="study-group-manager">
      <h2>👥 Study Group Management</h2>
      
      {/* Create Study Group Form */}
      <div className="create-group-form">
        <h3>Create New Study Group</h3>
        <form onSubmit={async (e) => {
          e.preventDefault();
          const formData = new FormData(e.target);
          const groupData = {
            groupName: formData.get('groupName'),
            description: formData.get('description'),
            subject: formData.get('subject'),
            gradeLevel: formData.get('gradeLevel'),
            topic: formData.get('topic'),
            groupType: formData.get('groupType'),
            maxMembers: parseInt(formData.get('maxMembers')),
            isPublic: formData.get('isPublic') === 'on',
            meetingFrequency: formData.get('meetingFrequency'),
            preferredMeetingDays: formData.get('preferredMeetingDays'),
            preferredMeetingTime: formData.get('preferredMeetingTime'),
            location: formData.get('location'),
            requirements: formData.get('requirements'),
            tags: formData.get('tags'),
            isActive: true,
            creatorId: parseInt(formData.get('creatorId'))
          };
          
          await createStudyGroup(groupData);
          e.target.reset();
        }}>
          <input name="groupName" type="text" placeholder="Group Name" required />
          <textarea name="description" placeholder="Description" required />
          <input name="subject" type="text" placeholder="Subject" required />
          <input name="gradeLevel" type="text" placeholder="Grade Level" required />
          <input name="topic" type="text" placeholder="Topic" required />
          
          <select name="groupType" required>
            <option value="">Select Group Type</option>
            <option value="STUDY_GROUP">Study Group</option>
            <option value="PROJECT_GROUP">Project Group</option>
            <option value="DISCUSSION_GROUP">Discussion Group</option>
            <option value="TUTORING_GROUP">Tutoring Group</option>
            <option value="PEER_LEARNING">Peer Learning</option>
          </select>
          
          <input name="maxMembers" type="number" placeholder="Max Members" min="2" max="50" />
          
          <label>
            <input name="isPublic" type="checkbox" />
            Public Group
          </label>
          
          <select name="meetingFrequency">
            <option value="">Select Meeting Frequency</option>
            <option value="DAILY">Daily</option>
            <option value="WEEKLY">Weekly</option>
            <option value="BIWEEKLY">Bi-weekly</option>
            <option value="MONTHLY">Monthly</option>
          </select>
          
          <input name="preferredMeetingDays" type="text" placeholder="Preferred Days (e.g., MONDAY,TUESDAY)" />
          <input name="preferredMeetingTime" type="time" placeholder="Preferred Time" />
          <input name="location" type="text" placeholder="Location" />
          <textarea name="requirements" placeholder="Requirements" />
          <input name="tags" type="text" placeholder="Tags (comma-separated)" />
          <input name="creatorId" type="number" placeholder="Creator Student ID" required />
          
          <button type="submit">Create Study Group</button>
        </form>
      </div>

      {/* Study Groups Grid */}
      <div className="study-groups-grid">
        <h3>Available Study Groups</h3>
        <div className="groups-list">
          {studyGroups.map((group) => (
            <div key={group.id} className="study-group-card">
              <div className="group-header">
                <h4>{group.groupName}</h4>
                <span className={`group-status status-${group.groupStatus.toLowerCase()}`}>
                  {group.groupStatus}
                </span>
              </div>
              
              <p className="group-description">{group.description}</p>
              
              <div className="group-details">
                <div className="detail">
                  <span className="label">Subject:</span>
                  <span className="value">{group.subject}</span>
                </div>
                <div className="detail">
                  <span className="label">Grade:</span>
                  <span className="value">{group.gradeLevel}</span>
                </div>
                <div className="detail">
                  <span className="label">Topic:</span>
                  <span className="value">{group.topic}</span>
                </div>
                <div className="detail">
                  <span className="label">Type:</span>
                  <span className="value">{group.groupType}</span>
                </div>
                <div className="detail">
                  <span className="label">Members:</span>
                  <span className="value">{group.currentMembers}/{group.maxMembers}</span>
                </div>
              </div>
              
              <div className="group-actions">
                <button onClick={() => {/* Join group */}}>Join Group</button>
                <button onClick={() => {/* View details */}}>View Details</button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default StudyGroupManager;
```

### 2. Study Session Management

```jsx
// components/peer-learning/StudySessionManager.jsx
import React, { useState, useEffect } from 'react';

const StudySessionManager = () => {
  const [studySessions, setStudySessions] = useState([]);
  const [studyGroups, setStudyGroups] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStudySessions();
    fetchStudyGroups();
  }, []);

  const fetchStudySessions = async () => {
    try {
      const response = await fetch(
        'http://localhost:9091/api/v1/peer-learning/study-sessions?page=0&size=10',
        { headers: authAPI.getAuthHeaders() }
      );
      
      if (response.ok) {
        const data = await response.json();
        setStudySessions(data.content || []);
      }
    } catch (error) {
      console.error('Error fetching study sessions:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchStudyGroups = async () => {
    try {
      const response = await fetch(
        'http://localhost:9091/api/v1/peer-learning/study-groups?page=0&size=20',
        { headers: authAPI.getAuthHeaders() }
      );
      
      if (response.ok) {
        const data = await response.json();
        setStudyGroups(data.content || []);
      }
    } catch (error) {
      console.error('Error fetching study groups:', error);
    }
  };

  const createStudySession = async (sessionData) => {
    try {
      const response = await fetch('http://localhost:9091/api/v1/peer-learning/study-sessions', {
        method: 'POST',
        headers: authAPI.getAuthHeaders(),
        body: JSON.stringify(sessionData)
      });
      
      if (response.ok) {
        fetchStudySessions(); // Refresh the list
        return await response.json();
      }
    } catch (error) {
      console.error('Error creating study session:', error);
    }
  };

  if (loading) return <div>Loading study sessions...</div>;

  return (
    <div className="study-session-manager">
      <h2>📚 Study Session Management</h2>
      
      {/* Create Study Session Form */}
      <div className="create-session-form">
        <h3>Create New Study Session</h3>
        <form onSubmit={async (e) => {
          e.preventDefault();
          const formData = new FormData(e.target);
          const sessionData = {
            studyGroupId: parseInt(formData.get('studyGroupId')),
            sessionTitle: formData.get('sessionTitle'),
            description: formData.get('description'),
            topic: formData.get('topic'),
            sessionType: formData.get('sessionType'),
            scheduledDateTime: formData.get('scheduledDateTime'),
            durationMinutes: parseInt(formData.get('durationMinutes')),
            location: formData.get('location'),
            maxParticipants: parseInt(formData.get('maxParticipants')),
            agenda: formData.get('agenda'),
            materialsNeeded: formData.get('materialsNeeded'),
            isOnline: formData.get('isOnline') === 'on',
            meetingLink: formData.get('meetingLink'),
            sessionStatus: 'SCHEDULED',
            facilitatorId: parseInt(formData.get('facilitatorId'))
          };
          
          await createStudySession(sessionData);
          e.target.reset();
        }}>
          <select name="studyGroupId" required>
            <option value="">Select Study Group</option>
            {studyGroups.map((group) => (
              <option key={group.id} value={group.id}>
                {group.groupName} - {group.subject}
              </option>
            ))}
          </select>
          
          <input name="sessionTitle" type="text" placeholder="Session Title" required />
          <textarea name="description" placeholder="Description" required />
          <input name="topic" type="text" placeholder="Topic" required />
          
          <select name="sessionType" required>
            <option value="">Select Session Type</option>
            <option value="STUDY_SESSION">Study Session</option>
            <option value="TUTORING_SESSION">Tutoring Session</option>
            <option value="DISCUSSION">Discussion</option>
            <option value="WORKSHOP">Workshop</option>
            <option value="REVIEW_SESSION">Review Session</option>
            <option value="PROJECT_MEETING">Project Meeting</option>
            <option value="MENTORSHIP">Mentorship</option>
          </select>
          
          <input name="scheduledDateTime" type="datetime-local" required />
          <input name="durationMinutes" type="number" placeholder="Duration (minutes)" required />
          <input name="location" type="text" placeholder="Location" />
          <input name="maxParticipants" type="number" placeholder="Max Participants" min="2" />
          
          <label>
            <input name="isOnline" type="checkbox" />
            Online Session
          </label>
          
          <input name="meetingLink" type="url" placeholder="Meeting Link (if online)" />
          <textarea name="agenda" placeholder="Session Agenda" />
          <textarea name="materialsNeeded" placeholder="Materials Needed" />
          <input name="facilitatorId" type="number" placeholder="Facilitator Student ID" required />
          
          <button type="submit">Create Study Session</button>
        </form>
      </div>

      {/* Study Sessions List */}
      <div className="study-sessions-list">
        <h3>Upcoming Study Sessions</h3>
        <div className="sessions-grid">
          {studySessions.map((session) => (
            <div key={session.id} className="study-session-card">
              <div className="session-header">
                <h4>{session.sessionTitle}</h4>
                <span className={`session-status status-${session.sessionStatus.toLowerCase()}`}>
                  {session.sessionStatus}
                </span>
              </div>
              
              <p className="session-description">{session.description}</p>
              
              <div className="session-details">
                <div className="detail">
                  <span className="label">Topic:</span>
                  <span className="value">{session.topic}</span>
                </div>
                <div className="detail">
                  <span className="label">Type:</span>
                  <span className="value">{session.sessionType}</span>
                </div>
                <div className="detail">
                  <span className="label">Scheduled:</span>
                  <span className="value">{new Date(session.scheduledDateTime).toLocaleString()}</span>
                </div>
                <div className="detail">
                  <span className="label">Duration:</span>
                  <span className="value">{session.durationMinutes} minutes</span>
                </div>
                <div className="detail">
                  <span className="label">Location:</span>
                  <span className="value">{session.location || 'Online'}</span>
                </div>
                <div className="detail">
                  <span className="label">Participants:</span>
                  <span className="value">{session.currentParticipants}/{session.maxParticipants}</span>
                </div>
              </div>
              
              <div className="session-actions">
                <button onClick={() => {/* Join session */}}>Join Session</button>
                <button onClick={() => {/* View details */}}>View Details</button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default StudySessionManager;
```

---

## 🎨 CSS Styling

### Basic Styles for All Components

```css
/* styles/components.css */

/* Dashboard Analytics */
.dashboard-analytics {
  padding: 20px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.metric-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.metric-value {
  font-size: 2rem;
  font-weight: bold;
  margin: 10px 0;
}

.completion-rates {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 30px;
}

.rate-item {
  margin-bottom: 15px;
}

.progress-bar {
  width: 100%;
  height: 20px;
  background-color: #e9ecef;
  border-radius: 10px;
  overflow: hidden;
  margin-top: 5px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #28a745, #20c997);
  transition: width 0.3s ease;
}

/* Forms */
.create-session-form,
.create-group-form,
.create-achievement-form {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 30px;
}

.create-session-form form,
.create-group-form form,
.create-achievement-form form {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 15px;
}

.create-session-form input,
.create-session-form select,
.create-session-form textarea,
.create-group-form input,
.create-group-form select,
.create-group-form textarea,
.create-achievement-form input,
.create-achievement-form select,
.create-achievement-form textarea {
  padding: 10px;
  border: 1px solid #ced4da;
  border-radius: 5px;
  font-size: 14px;
}

.create-session-form button,
.create-group-form button,
.create-achievement-form button {
  grid-column: 1 / -1;
  padding: 12px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  transition: background 0.3s ease;
}

.create-session-form button:hover,
.create-group-form button:hover,
.create-achievement-form button:hover {
  background: #0056b3;
}

/* Cards */
.sessions-grid,
.groups-list,
.achievements-list,
.sessions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.session-card,
.study-group-card,
.achievement-card,
.study-session-card {
  background: white;
  border: 1px solid #e9ecef;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.session-card:hover,
.study-group-card:hover,
.achievement-card:hover,
.study-session-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

/* Leaderboard */
.leaderboard-list {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.leaderboard-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #e9ecef;
}

.leaderboard-item:last-child {
  border-bottom: none;
}

.leaderboard-item.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
}

.leaderboard-item.rank-2 {
  background: linear-gradient(135deg, #c0c0c0, #e5e5e5);
}

.leaderboard-item.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #daa520);
}

.rank {
  font-size: 1.5rem;
  font-weight: bold;
  margin-right: 20px;
  min-width: 60px;
}

.student-info {
  flex: 1;
  margin-right: 20px;
}

.stats {
  display: flex;
  gap: 20px;
}

.stat {
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 0.8rem;
  color: #6c757d;
}

.stat-value {
  display: block;
  font-size: 1.2rem;
  font-weight: bold;
  color: #495057;
}

/* Achievement Cards */
.achievement-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.achievement-icon {
  font-size: 2rem;
}

.rarity-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: bold;
  text-transform: uppercase;
}

.rarity-common { background: #6c757d; color: white; }
.rarity-uncommon { background: #28a745; color: white; }
.rarity-rare { background: #007bff; color: white; }
.rarity-epic { background: #6f42c1; color: white; }
.rarity-legendary { background: #fd7e14; color: white; }

.achievement-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  margin: 15px 0;
}

.achievement-flags {
  display: flex;
  gap: 10px;
}

.flag {
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 0.7rem;
  font-weight: bold;
}

.flag.repeatable { background: #28a745; color: white; }
.flag.secret { background: #dc3545; color: white; }

/* Study Groups */
.group-status,
.session-status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: bold;
  text-transform: uppercase;
}

.status-active { background: #28a745; color: white; }
.status-inactive { background: #6c757d; color: white; }
.status-scheduled { background: #007bff; color: white; }
.status-completed { background: #28a745; color: white; }
.status-cancelled { background: #dc3545; color: white; }

.group-details,
.session-details {
  margin: 15px 0;
}

.detail {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.detail .label {
  font-weight: bold;
  color: #6c757d;
}

.detail .value {
  color: #495057;
}

/* Actions */
.session-actions,
.group-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.session-actions button,
.group-actions button {
  padding: 8px 16px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s ease;
}

.session-actions button:first-child,
.group-actions button:first-child {
  background: #28a745;
  color: white;
}

.session-actions button:last-child,
.group-actions button:last-child {
  background: #007bff;
  color: white;
}

.session-actions button:hover,
.group-actions button:hover {
  opacity: 0.8;
}

/* Responsive Design */
@media (max-width: 768px) {
  .metrics-grid {
    grid-template-columns: 1fr;
  }
  
  .create-session-form form,
  .create-group-form form,
  .create-achievement-form form {
    grid-template-columns: 1fr;
  }
  
  .sessions-grid,
  .groups-list,
  .achievements-list {
    grid-template-columns: 1fr;
  }
  
  .leaderboard-item {
    flex-direction: column;
    text-align: center;
  }
  
  .rank {
    margin-right: 0;
    margin-bottom: 10px;
  }
  
  .student-info {
    margin-right: 0;
    margin-bottom: 10px;
  }
}
```

---

## 🚀 Integration Example

### Main App Component

```jsx
// App.jsx
import React, { useState, useEffect } from 'react';
import { authAPI } from './config/api';
import DashboardAnalytics from './components/tutoring/DashboardAnalytics';
import TutoringSessionManager from './components/tutoring/TutoringSessionManager';
import LearningPathManager from './components/tutoring/LearningPathManager';
import Leaderboard from './components/gamification/Leaderboard';
import AchievementManager from './components/gamification/AchievementManager';
import StudyGroupManager from './components/peer-learning/StudyGroupManager';
import StudySessionManager from './components/peer-learning/StudySessionManager';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [currentUser, setCurrentUser] = useState(null);
  const [activeTab, setActiveTab] = useState('dashboard');

  useEffect(() => {
    // Check if user is already logged in
    const token = localStorage.getItem('jwtToken');
    if (token) {
      setIsAuthenticated(true);
      // You can decode the JWT token to get user info
    }
  }, []);

  const handleLogin = async (usernameOrEmail, password) => {
    try {
      const userData = await authAPI.login(usernameOrEmail, password);
      setCurrentUser(userData);
      setIsAuthenticated(true);
    } catch (error) {
      console.error('Login failed:', error);
      alert('Login failed. Please check your credentials.');
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('jwtToken');
    setIsAuthenticated(false);
    setCurrentUser(null);
  };

  if (!isAuthenticated) {
    return (
      <div className="login-container">
        <div className="login-form">
          <h2>🎓 School Management System</h2>
          <form onSubmit={(e) => {
            e.preventDefault();
            const formData = new FormData(e.target);
            handleLogin(formData.get('username'), formData.get('password'));
          }}>
            <input name="username" type="text" placeholder="Username or Email" required />
            <input name="password" type="password" placeholder="Password" required />
            <button type="submit">Login</button>
          </form>
        </div>
      </div>
    );
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>🎓 School Management System</h1>
        <nav className="nav-tabs">
          <button 
            className={activeTab === 'dashboard' ? 'active' : ''}
            onClick={() => setActiveTab('dashboard')}
          >
            📊 Dashboard
          </button>
          <button 
            className={activeTab === 'tutoring' ? 'active' : ''}
            onClick={() => setActiveTab('tutoring')}
          >
            🎓 Tutoring
          </button>
          <button 
            className={activeTab === 'gamification' ? 'active' : ''}
            onClick={() => setActiveTab('gamification')}
          >
            🎮 Gamification
          </button>
          <button 
            className={activeTab === 'peer-learning' ? 'active' : ''}
            onClick={() => setActiveTab('peer-learning')}
          >
            👥 Peer Learning
          </button>
        </nav>
        <div className="user-info">
          <span>Welcome, {currentUser?.username || 'User'}!</span>
          <button onClick={handleLogout}>Logout</button>
        </div>
      </header>

      <main className="app-main">
        {activeTab === 'dashboard' && <DashboardAnalytics />}
        {activeTab === 'tutoring' && (
          <div className="tutoring-section">
            <TutoringSessionManager />
            <LearningPathManager />
          </div>
        )}
        {activeTab === 'gamification' && (
          <div className="gamification-section">
            <Leaderboard />
            <AchievementManager />
          </div>
        )}
        {activeTab === 'peer-learning' && (
          <div className="peer-learning-section">
            <StudyGroupManager />
            <StudySessionManager />
          </div>
        )}
      </main>
    </div>
  );
}

export default App;
```

---

## 📝 API Endpoints Summary

### Academic Tutoring System
- `GET /api/v1/tutoring/dashboard/analytics` - Dashboard analytics
- `POST /api/v1/tutoring/sessions` - Create tutoring session
- `GET /api/v1/tutoring/sessions` - Get all tutoring sessions
- `POST /api/v1/tutoring/learning-paths` - Create learning path
- `GET /api/v1/tutoring/learning-paths` - Get all learning paths
- `POST /api/v1/tutoring/learning-modules` - Create learning module
- `GET /api/v1/tutoring/learning-modules` - Get learning modules

### Gamification System
- `POST /api/v1/gamification/achievements` - Create achievement
- `GET /api/v1/gamification/achievements` - Get all achievements
- `GET /api/v1/gamification/leaderboard` - Get leaderboard

### Peer Learning Platform
- `POST /api/v1/peer-learning/study-groups` - Create study group
- `GET /api/v1/peer-learning/study-groups` - Get all study groups
- `POST /api/v1/peer-learning/study-sessions` - Create study session
- `GET /api/v1/peer-learning/study-sessions` - Get all study sessions

## 🔧 Setup Instructions

1. **Install Dependencies:**
   ```bash
   npm install react react-dom
   npm install axios  # Alternative to fetch
   ```

2. **Create the component files** as shown above

3. **Add the CSS styles** to your stylesheet

4. **Set up routing** if using React Router:
   ```bash
   npm install react-router-dom
   ```

5. **Start the development server:**
   ```bash
   npm start
   ```

This documentation provides a complete guide for integrating the React frontend with the three newly implemented systems. All components are designed to work with the current API endpoints and follow modern React patterns with hooks and functional components.
