import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import Players from './pages/Players';
import Teams from './pages/Teams';
import MatchDetails from './pages/MatchDetails';
import ScorerConsole from './pages/ScorerConsole';
import Leaderboard from './pages/Leaderboard';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="app-container">
          <Navbar />
          <main style={styles.mainContent}>
            <Routes>
              {/* Public/Authenticated routes */}
              <Route path="/" element={
                <ProtectedRoute>
                  <Dashboard />
                </ProtectedRoute>
              } />
              
              <Route path="/players" element={
                <ProtectedRoute>
                  <Players />
                </ProtectedRoute>
              } />

              <Route path="/teams" element={
                <ProtectedRoute>
                  <Teams />
                </ProtectedRoute>
              } />

              <Route path="/leaderboards" element={
                <ProtectedRoute>
                  <Leaderboard />
                </ProtectedRoute>
              } />

              <Route path="/matches/:id" element={
                <ProtectedRoute>
                  <MatchDetails />
                </ProtectedRoute>
              } />

              {/* Scorer only protected route */}
              <Route path="/matches/:id/scorer" element={
                <ProtectedRoute allowedRoles={['ADMIN', 'SCORER']}>
                  <ScorerConsole />
                </ProtectedRoute>
              } />

              {/* Auth routes */}
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />

              {/* Fallback */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
        </div>
      </Router>
    </AuthProvider>
  );
}

const styles = {
  mainContent: {
    flex: 1,
    display: 'flex',
    flexDirection: 'column',
    minHeight: 'calc(100vh - 73px)', // minus navbar height
  }
};

export default App;
