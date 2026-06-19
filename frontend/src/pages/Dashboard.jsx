import React, { useState, useEffect, useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../services/api';
import { AuthContext } from '../context/AuthContext';

const Dashboard = () => {
  const { user } = useContext(AuthContext);
  const [matches, setMatches] = useState([]);
  const [teams, setTeams] = useState([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  // Create match modal state
  const [showAddForm, setShowAddForm] = useState(false);
  const [team1Id, setTeam1Id] = useState('');
  const [team2Id, setTeam2Id] = useState('');
  const [matchDate, setMatchDate] = useState('');
  const [formError, setFormError] = useState('');

  const fetchMatches = async () => {
    setLoading(true);
    try {
      const response = await api.get('/api/v1/matches');
      // By default get matches, if endpoint lists matches
      setMatches(response.data || []);
    } catch (err) {
      console.error('Failed to fetch matches', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchTeams = async () => {
    try {
      const response = await api.get('/api/v1/teams');
      setTeams(response.data || []);
    } catch (err) {
      console.error('Failed to fetch teams', err);
    }
  };

  useEffect(() => {
    fetchMatches();
    fetchTeams();
  }, []);

  const handleScheduleMatch = async (e) => {
    e.preventDefault();
    setFormError('');
    if (team1Id === team2Id) {
      setFormError('Team 1 and Team 2 must be different!');
      return;
    }
    try {
      await api.post('/api/v1/matches', {
        team1Id,
        team2Id,
        matchDate: new Date(matchDate).toISOString()
      });
      setTeam1Id('');
      setTeam2Id('');
      setMatchDate('');
      setShowAddForm(false);
      fetchMatches();
    } catch (err) {
      setFormError(err.response?.data?.message || 'Failed to schedule match');
    }
  };

  const isScorerOrAdmin = user && (user.role === 'ADMIN' || user.role === 'SCORER');

  const getStatusBadgeClass = (status) => {
    if (['IN_PROGRESS', 'TOSS_PENDING', 'INNINGS_BREAK'].includes(status)) return 'badge-live';
    if (status === 'SCHEDULED') return 'badge-scheduled';
    return 'badge-completed';
  };

  const getStatusText = (status) => {
    if (status === 'IN_PROGRESS') return 'LIVE';
    if (status === 'TOSS_PENDING') return 'Toss Pending';
    if (status === 'INNINGS_BREAK') return 'Innings Break';
    return status;
  };

  return (
    <div style={{ padding: '24px 32px', maxWidth: '1200px', margin: '0 auto', width: '100%' }}>
      <div style={styles.header}>
        <h1>Match Dashboard</h1>
        {isScorerOrAdmin && (
          <button className="btn btn-primary" onClick={() => setShowAddForm(!showAddForm)}>
            {showAddForm ? 'Close Form' : 'Schedule New Match'}
          </button>
        )}
      </div>

      {showAddForm && (
        <div className="card" style={{ maxWidth: '650px', margin: '0 auto 24px auto' }}>
          <h3>Schedule Match</h3>
          {formError && <div style={styles.error}>{formError}</div>}
          <form onSubmit={handleScheduleMatch}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
              <div className="form-group">
                <label>Team 1 (Home) *</label>
                <select className="form-control" value={team1Id} onChange={(e) => setTeam1Id(e.target.value)} required style={styles.select}>
                  <option value="">Select home team...</option>
                  {teams.map(t => (
                    <option key={t.id} value={t.id}>{t.name}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Team 2 (Away) *</label>
                <select className="form-control" value={team2Id} onChange={(e) => setTeam2Id(e.target.value)} required style={styles.select}>
                  <option value="">Select away team...</option>
                  {teams.map(t => (
                    <option key={t.id} value={t.id}>{t.name}</option>
                  ))}
                </select>
              </div>
            </div>
            <div className="form-group">
              <label>Match Date & Time *</label>
              <input
                type="datetime-local"
                className="form-control"
                value={matchDate}
                onChange={(e) => setMatchDate(e.target.value)}
                required
              />
            </div>
            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
              Schedule Match
            </button>
          </form>
        </div>
      )}

      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px' }}>Loading matches...</div>
      ) : matches.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '60px' }}>
          <p style={{ color: 'var(--text-muted)', marginBottom: '16px' }}>No matches found on the platform yet.</p>
          {isScorerOrAdmin && <p>Click "Schedule New Match" to create the first match!</p>}
        </div>
      ) : (
        <div style={styles.grid}>
          {matches.map((match) => (
            <div key={match.id} className="card" style={styles.matchCard}>
              <div style={styles.cardHeader}>
                <span className={`badge ${getStatusBadgeClass(match.status)}`}>
                  {getStatusText(match.status)}
                </span>
                <span style={styles.matchDate}>{new Date(match.matchDate).toLocaleDateString()}</span>
              </div>
              
              <div style={styles.versusContainer}>
                <div style={styles.teamLine}>
                  <span style={styles.teamName}>{match.team1Name}</span>
                </div>
                <div style={styles.vsBadge}>VS</div>
                <div style={styles.teamLine}>
                  <span style={styles.teamName}>{match.team2Name}</span>
                </div>
              </div>

              {match.resultMarginDetail && (
                <div style={styles.result}>{match.resultMarginDetail}</div>
              )}

              <div style={styles.actions}>
                <Link to={`/matches/${match.id}`} className="btn btn-secondary" style={{ flex: 1 }}>
                  View Scorecard
                </Link>
                {isScorerOrAdmin && match.status !== 'COMPLETED' && (
                  <Link to={`/matches/${match.id}/scorer`} className="btn btn-primary" style={{ flex: 1 }}>
                    Scoring Console
                  </Link>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

const styles = {
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '24px',
  },
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(360px, 1fr))',
    gap: '24px',
  },
  matchCard: {
    marginBottom: 0,
    background: 'rgba(30, 41, 59, 0.4)',
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'space-between',
    minHeight: '230px',
  },
  cardHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '16px',
  },
  matchDate: {
    fontSize: '0.85rem',
    color: 'var(--text-muted)',
  },
  versusContainer: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    gap: '8px',
    margin: '12px 0',
  },
  teamLine: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    width: '100%',
  },
  teamName: {
    fontWeight: '700',
    fontSize: '1.2rem',
    textAlign: 'center',
  },
  vsBadge: {
    fontSize: '0.75rem',
    fontWeight: '800',
    background: 'rgba(255, 255, 255, 0.05)',
    border: '1px solid var(--border-glass)',
    padding: '2px 8px',
    borderRadius: '10px',
    color: 'var(--accent-gold)',
  },
  result: {
    textAlign: 'center',
    fontSize: '0.9rem',
    color: 'var(--accent-gold)',
    fontWeight: 600,
    margin: '8px 0',
    padding: '6px',
    background: 'rgba(245, 158, 11, 0.05)',
    borderRadius: '6px',
  },
  actions: {
    display: 'flex',
    gap: '12px',
    marginTop: '16px',
  },
  select: {
    background: '#1f2937',
    color: 'white',
  },
  error: {
    padding: '10px',
    background: 'rgba(239, 68, 68, 0.1)',
    color: 'var(--accent-red)',
    borderRadius: '6px',
    marginBottom: '16px',
  }
};

export default Dashboard;
