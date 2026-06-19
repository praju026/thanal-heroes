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

  // Create match form states
  const [showAddForm, setShowAddForm] = useState(false);
  const [isQuickMatch, setIsQuickMatch] = useState(true);
  const [team1Id, setTeam1Id] = useState('');
  const [team2Id, setTeam2Id] = useState('');
  const [matchDate, setMatchDate] = useState('');
  const [overs, setOvers] = useState(4);
  const [tossWinnerId, setTossWinnerId] = useState('');
  const [tossDecision, setTossDecision] = useState('BAT');
  const [formError, setFormError] = useState('');

  const fetchMatches = async () => {
    setLoading(true);
    try {
      const response = await api.get('/api/v1/matches');
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

  const handleSubmitMatchForm = async (e) => {
    e.preventDefault();
    setFormError('');
    if (!team1Id || !team2Id) {
      setFormError('Please select both teams!');
      return;
    }
    if (team1Id === team2Id) {
      setFormError('Team 1 and Team 2 must be different!');
      return;
    }

    try {
      if (isQuickMatch) {
        if (!tossWinnerId) {
          setFormError('Please select who won the toss!');
          return;
        }
        const response = await api.post(`/api/v1/matches/quick-start?team1Id=${team1Id}&team2Id=${team2Id}&overs=${overs}&tossWinnerId=${tossWinnerId}&tossDecision=${tossDecision}`);
        setTeam1Id('');
        setTeam2Id('');
        setTossWinnerId('');
        setShowAddForm(false);
        // Redirect directly to the live scorer console!
        navigate(`/matches/${response.data.id}/scorer`);
      } else {
        if (!matchDate) {
          setFormError('Please select a match date and time!');
          return;
        }
        await api.post('/api/v1/matches', {
          team1Id,
          team2Id,
          matchDate: new Date(matchDate).toISOString(),
          overs: parseInt(overs)
        });
        setTeam1Id('');
        setTeam2Id('');
        setMatchDate('');
        setShowAddForm(false);
        fetchMatches();
      }
    } catch (err) {
      setFormError(err.response?.data?.message || 'Failed to start/schedule match');
    }
  };

  const handleDeleteMatch = async (matchId) => {
    if (!window.confirm("Are you sure you want to permanently delete this match and all its scorecard history?")) return;
    try {
      await api.delete(`/api/v1/matches/${matchId}`);
      fetchMatches();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to delete match');
    }
  };

  const isScorerOrAdmin = user && (user.role === 'ADMIN' || user.role === 'SCORER');
  const isAdmin = user && user.role === 'ADMIN';

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
            {showAddForm ? 'Close Form' : '⚡ Start/Schedule Match'}
          </button>
        )}
      </div>

      {showAddForm && (
        <div className="card" style={{ maxWidth: '650px', margin: '0 auto 24px auto' }}>
          {/* Quick tabs */}
          <div style={{ display: 'flex', borderBottom: '1px solid var(--border-glass)', marginBottom: '20px' }}>
            <button
              type="button"
              onClick={() => { setIsQuickMatch(true); setFormError(''); }}
              style={{
                flex: 1,
                padding: '12px',
                background: 'none',
                border: 'none',
                color: isQuickMatch ? 'var(--accent-green)' : 'var(--text-muted)',
                borderBottom: isQuickMatch ? '2px solid var(--accent-green)' : 'none',
                fontWeight: 'bold',
                cursor: 'pointer'
              }}
            >
              ⚡ Quick Match (Instant Play)
            </button>
            <button
              type="button"
              onClick={() => { setIsQuickMatch(false); setFormError(''); }}
              style={{
                flex: 1,
                padding: '12px',
                background: 'none',
                border: 'none',
                color: !isQuickMatch ? 'var(--accent-green)' : 'var(--text-muted)',
                borderBottom: !isQuickMatch ? '2px solid var(--accent-green)' : 'none',
                fontWeight: 'bold',
                cursor: 'pointer'
              }}
            >
              📅 Schedule Match (Future)
            </button>
          </div>

          <h3>{isQuickMatch ? 'Start Quick Match Now' : 'Schedule Future Match'}</h3>
          {formError && <div style={styles.error}>{formError}</div>}

          <form onSubmit={handleSubmitMatchForm}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginBottom: '12px' }}>
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

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginBottom: '12px' }}>
              <div className="form-group">
                <label>Overs per Innings *</label>
                <select className="form-control" value={overs} onChange={(e) => setOvers(parseInt(e.target.value))} required style={styles.select}>
                  <option value={1}>1 Over</option>
                  <option value={2}>2 Overs</option>
                  <option value={3}>3 Overs</option>
                  <option value={4}>4 Overs</option>
                  <option value={5}>5 Overs</option>
                  <option value={10}>10 Overs</option>
                  <option value={15}>15 Overs</option>
                  <option value={20}>20 Overs</option>
                </select>
              </div>

              {!isQuickMatch ? (
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
              ) : (
                <div className="form-group">
                  <label>Toss Winner *</label>
                  <select className="form-control" value={tossWinnerId} onChange={(e) => setTossWinnerId(e.target.value)} required style={styles.select}>
                    <option value="">Select toss winner...</option>
                    {team1Id && <option value={team1Id}>{teams.find(t => t.id === team1Id)?.name}</option>}
                    {team2Id && <option value={team2Id}>{teams.find(t => t.id === team2Id)?.name}</option>}
                  </select>
                </div>
              )}
            </div>

            {isQuickMatch && (
              <div className="form-group" style={{ marginBottom: '16px' }}>
                <label>Toss Decision *</label>
                <select className="form-control" value={tossDecision} onChange={(e) => setTossDecision(e.target.value)} required style={styles.select}>
                  <option value="BAT">Bat First</option>
                  <option value="BOWL">Bowl First</option>
                </select>
              </div>
            )}

            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
              {isQuickMatch ? '⚡ Start Match Instantly' : 'Schedule Match'}
            </button>
          </form>
        </div>
      )}

      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px' }}>Loading matches...</div>
      ) : matches.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '60px' }}>
          <p style={{ color: 'var(--text-muted)', marginBottom: '16px' }}>No matches found on the platform yet.</p>
          {isScorerOrAdmin && <p>Click "⚡ Start/Schedule Match" to create the first match!</p>}
        </div>
      ) : (
        <div style={styles.grid}>
          {matches.map((match) => (
            <div key={match.id} className="card" style={styles.matchCard}>
              <div style={styles.cardHeader}>
                <span className={`badge ${getStatusBadgeClass(match.status)}`}>
                  {getStatusText(match.status)}
                </span>
                <span style={styles.matchDate}>
                  {new Date(match.matchDate).toLocaleDateString()} ({match.overs || 20} ov)
                </span>
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
                <Link to={`/matches/${match.id}`} className="btn btn-secondary" style={{ flex: 2 }}>
                  View Scorecard
                </Link>
                {isScorerOrAdmin && match.status !== 'COMPLETED' && (
                  <Link to={`/matches/${match.id}/scorer`} className="btn btn-primary" style={{ flex: 2 }}>
                    Scoring
                  </Link>
                )}
                {isAdmin && (
                  <button
                    onClick={() => handleDeleteMatch(match.id)}
                    className="btn btn-secondary"
                    style={{ flex: 1, padding: '8px', color: 'var(--accent-red)', border: '1px solid rgba(239, 68, 68, 0.2)' }}
                    title="Delete Match"
                  >
                    🗑️
                  </button>
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
