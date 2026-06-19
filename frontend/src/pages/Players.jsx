import React, { useState, useEffect, useContext } from 'react';
import api from '../services/api';
import { AuthContext } from '../context/AuthContext';

const Players = () => {
  const { user } = useContext(AuthContext);
  const [players, setPlayers] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(false);
  
  // Detail modal state
  const [selectedPlayer, setSelectedPlayer] = useState(null);
  const [careerStats, setCareerStats] = useState(null);
  const [statsLoading, setStatsLoading] = useState(false);

  // Create player form state
  const [showAddForm, setShowAddForm] = useState(false);
  const [newName, setNewName] = useState('');
  const [newBattingStyle, setNewBattingStyle] = useState('RIGHT_HAND');
  const [newBowlingStyle, setNewBowlingStyle] = useState('RIGHT_ARM_FAST');
  const [profilePic, setProfilePic] = useState('');
  const [formError, setFormError] = useState('');

  // Edit player form state
  const [editingPlayer, setEditingPlayer] = useState(null);
  const [editName, setEditName] = useState('');
  const [editBattingStyle, setEditBattingStyle] = useState('RIGHT_HAND');
  const [editBowlingStyle, setEditBowlingStyle] = useState('RIGHT_ARM_FAST');
  const [editProfilePic, setEditProfilePic] = useState('');

  const fetchPlayers = async (nameQuery = '') => {
    setLoading(true);
    try {
      const response = await api.get(`/api/v1/players?name=${nameQuery}&size=50`);
      setPlayers(response.data.content || []);
    } catch (err) {
      console.error('Failed to fetch players', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPlayers();
  }, []);

  const handleSearchChange = (e) => {
    setSearch(e.target.value);
    fetchPlayers(e.target.value);
  };

  const handleViewStats = async (player) => {
    setSelectedPlayer(player);
    setStatsLoading(true);
    setCareerStats(null);
    try {
      const response = await api.get(`/api/v1/players/${player.id}/career-stats`);
      setCareerStats(response.data);
    } catch (err) {
      console.error('Failed to fetch career stats', err);
    } finally {
      setStatsLoading(false);
    }
  };

  const handleAddPlayer = async (e) => {
    e.preventDefault();
    setFormError('');
    try {
      await api.post('/api/v1/players', {
        name: newName,
        battingStyle: newBattingStyle,
        bowlingStyle: newBowlingStyle,
        profilePictureUrl: profilePic || null,
      });
      setNewName('');
      setProfilePic('');
      setShowAddForm(false);
      fetchPlayers(search);
    } catch (err) {
      setFormError(err.response?.data?.message || 'Failed to add player');
    }
  };

  const handleStartEdit = (player) => {
    setEditingPlayer(player);
    setEditName(player.name);
    setEditBattingStyle(player.battingStyle || 'RIGHT_HAND');
    setEditBowlingStyle(player.bowlingStyle || 'RIGHT_ARM_FAST');
    setEditProfilePic(player.profilePictureUrl || '');
    setFormError('');
  };

  const handleUpdatePlayer = async (e) => {
    e.preventDefault();
    setFormError('');
    try {
      await api.put(`/api/v1/players/${editingPlayer.id}`, {
        name: editName,
        battingStyle: editBattingStyle,
        bowlingStyle: editBowlingStyle,
        profilePictureUrl: editProfilePic || null,
      });
      setEditingPlayer(null);
      fetchPlayers(search);
    } catch (err) {
      setFormError(err.response?.data?.message || 'Failed to update player');
    }
  };

  const handleDeletePlayer = async (id) => {
    if (window.confirm('Are you sure you want to delete this player profile? This will soft-delete the player.')) {
      try {
        await api.delete(`/api/v1/players/${id}`);
        fetchPlayers(search);
      } catch (err) {
        alert(err.response?.data?.message || 'Failed to delete player');
      }
    }
  };

  const isScorerOrAdmin = user && (user.role === 'ADMIN' || user.role === 'SCORER');

  return (
    <div style={{ padding: '24px 32px', maxW: '1200px', margin: '0 auto', width: '100%' }}>
      <div style={styles.header}>
        <h1>Player Directory</h1>
        {isScorerOrAdmin && (
          <button className="btn btn-primary" onClick={() => setShowAddForm(!showAddForm)}>
            {showAddForm ? 'Close Form' : 'Register New Player'}
          </button>
        )}
      </div>

      {showAddForm && (
        <div className="card" style={{ maxWidth: '600px', margin: '0 auto 24px auto' }}>
          <h3>Register Player Profile</h3>
          {formError && <div style={styles.error}>{formError}</div>}
          <form onSubmit={handleAddPlayer}>
            <div className="form-group">
              <label>Player Name *</label>
              <input
                type="text"
                className="form-control"
                value={newName}
                onChange={(e) => setNewName(e.target.value)}
                required
                placeholder="e.g. MS Dhoni"
              />
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
              <div className="form-group">
                <label>Batting Style</label>
                <select className="form-control" value={newBattingStyle} onChange={(e) => setNewBattingStyle(e.target.value)} style={styles.select}>
                  <option value="RIGHT_HAND">Right Hand Batsman</option>
                  <option value="LEFT_HAND">Left Hand Batsman</option>
                </select>
              </div>
              <div className="form-group">
                <label>Bowling Style</label>
                <select className="form-control" value={newBowlingStyle} onChange={(e) => setNewBowlingStyle(e.target.value)} style={styles.select}>
                  <option value="RIGHT_ARM_FAST">Right Arm Fast</option>
                  <option value="RIGHT_ARM_SPIN">Right Arm Off-Spin</option>
                  <option value="LEFT_ARM_FAST">Left Arm Fast</option>
                  <option value="LEFT_ARM_SPIN">Left Arm Spin</option>
                  <option value="NONE">None</option>
                </select>
              </div>
            </div>
            <div className="form-group">
              <label>Profile Picture URL</label>
              <input
                type="url"
                className="form-control"
                value={profilePic}
                onChange={(e) => setProfilePic(e.target.value)}
                placeholder="https://example.com/pic.jpg"
              />
            </div>
            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
              Create Player Profile
            </button>
          </form>
        </div>
      )}

      <div className="card" style={{ marginBottom: '24px' }}>
        <input
          type="text"
          className="form-control"
          value={search}
          onChange={handleSearchChange}
          placeholder="🔍 Search players by name..."
          style={{ paddingLeft: '40px' }}
        />
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px' }}>Loading players list...</div>
      ) : players.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>No players found. Register a player first!</div>
      ) : (
        <div style={styles.grid}>
          {players.map((player) => (
            <div key={player.id} className="card" style={styles.playerCard}>
              <div style={styles.playerInfo}>
                <div style={styles.avatar}>
                  {player.profilePictureUrl ? (
                    <img src={player.profilePictureUrl} alt={player.name} style={styles.avatarImg} />
                  ) : (
                    <span>{player.name.charAt(0).toUpperCase()}</span>
                  )}
                </div>
                <div>
                  <h3 style={{ marginBottom: '4px' }}>{player.name}</h3>
                  <div style={styles.styleBadges}>
                    <span style={styles.styleBadge}>{player.battingStyle?.replace('_', ' ')}</span>
                    {player.bowlingStyle !== 'NONE' && (
                      <span style={styles.styleBadge}>{player.bowlingStyle?.replace('_', ' ')}</span>
                    )}
                  </div>
                </div>
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', marginTop: '16px' }}>
                <button
                  className="btn btn-secondary"
                  style={{ width: '100%', fontSize: '0.85rem' }}
                  onClick={() => handleViewStats(player)}
                >
                  View Career Statistics
                </button>
                {isScorerOrAdmin && (
                  <div style={{ display: 'flex', gap: '8px', width: '100%' }}>
                    <button
                      className="btn btn-secondary"
                      style={{ flex: 1, fontSize: '0.82rem', padding: '8px 12px' }}
                      onClick={() => handleStartEdit(player)}
                    >
                      ✏️ Edit
                    </button>
                    {user && user.role === 'ADMIN' && (
                      <button
                        className="btn btn-danger"
                        style={{ flex: 1, fontSize: '0.82rem', padding: '8px 12px' }}
                        onClick={() => handleDeletePlayer(player.id)}
                      >
                        🗑️ Delete
                      </button>
                    )}
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Career Stats Detail Modal/Overlay */}
      {selectedPlayer && (
        <div style={styles.modalOverlay} onClick={() => setSelectedPlayer(null)}>
          <div style={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <div style={styles.modalHeader}>
              <h2>{selectedPlayer.name} - Career Stats</h2>
              <button style={styles.closeBtn} onClick={() => setSelectedPlayer(null)}>×</button>
            </div>

            {statsLoading ? (
              <div style={{ padding: '40px', textAlign: 'center' }}>Calculating career stats dynamically...</div>
            ) : careerStats ? (
              <div>
                {/* Batting Card */}
                <div className="card" style={{ borderLeft: '4px solid var(--accent-green)', padding: '16px', marginBottom: '16px' }}>
                  <h3 style={{ color: 'var(--accent-green)', marginBottom: '12px' }}>BATTING SUMMARY</h3>
                  <div style={styles.statsGrid}>
                    <div style={styles.statBox}>
                      <span style={styles.statVal}>{careerStats.batting.matchesPlayed}</span>
                      <span style={styles.statLabel}>Matches</span>
                    </div>
                    <div style={styles.statBox}>
                      <span style={styles.statVal}>{careerStats.batting.inningsPlayed}</span>
                      <span style={styles.statLabel}>Innings</span>
                    </div>
                    <div style={styles.statBox}>
                      <span style={styles.statVal}>{careerStats.batting.totalRuns}</span>
                      <span style={styles.statLabel}>Runs</span>
                    </div>
                    <div style={styles.statBox}>
                      <span style={styles.statVal}>{careerStats.batting.highestScore}</span>
                      <span style={styles.statLabel}>Highest Score</span>
                    </div>
                    <div style={styles.statBox}>
                      <span style={styles.statVal}>{careerStats.batting.average}</span>
                      <span style={styles.statLabel}>Average</span>
                    </div>
                    <div style={styles.statBox}>
                      <span style={styles.statVal}>{careerStats.batting.strikeRate}%</span>
                      <span style={styles.statLabel}>Strike Rate</span>
                    </div>
                    <div style={styles.statBox}>
                      <span style={styles.statVal}>{careerStats.batting.fifties}</span>
                      <span style={styles.statLabel}>50s</span>
                    </div>
                    <div style={styles.statBox}>
                      <span style={styles.statVal}>{careerStats.batting.hundreds}</span>
                      <span style={styles.statLabel}>100s</span>
                    </div>
                  </div>
                </div>

                {/* Bowling Card */}
                <div className="card" style={{ borderLeft: '4px solid var(--accent-gold)', padding: '16px' }}>
                  <h3 style={{ color: 'var(--accent-gold)', marginBottom: '12px' }}>BOWLING SUMMARY</h3>
                  <div style={styles.statsGrid}>
                    <div style={styles.statBox}>
                      <span style={styles.statVal}>{careerStats.bowling.inningsBowled}</span>
                      <span style={styles.statLabel}>Innings</span>
                    </div>
                    <div style={styles.statBox}>
                      <span style={styles.statVal}>{careerStats.bowling.oversBowled}</span>
                      <span style={styles.statLabel}>Overs</span>
                    </div>
                    <div style={styles.statBox}>
                      <span style={styles.statVal}>{careerStats.bowling.runsConceded}</span>
                      <span style={styles.statLabel}>Runs</span>
                    </div>
                    <div style={styles.statBox}>
                      <span style={styles.statVal}>{careerStats.bowling.wickets}</span>
                      <span style={styles.statLabel}>Wickets</span>
                    </div>
                    <div style={styles.statBox}>
                      <span style={styles.statVal}>{careerStats.bowling.bestBowling}</span>
                      <span style={styles.statLabel}>Best Bowl</span>
                    </div>
                    <div style={styles.statBox}>
                      <span style={styles.statVal}>{careerStats.bowling.economyRate}</span>
                      <span style={styles.statLabel}>Economy</span>
                    </div>
                    <div style={styles.statBox}>
                      <span style={styles.statVal}>{careerStats.bowling.average}</span>
                      <span style={styles.statLabel}>Average</span>
                    </div>
                    <div style={styles.statBox}>
                      <span style={styles.statVal}>{careerStats.bowling.strikeRate}</span>
                      <span style={styles.statLabel}>Strike Rate</span>
                    </div>
                  </div>
                </div>
              </div>
            ) : (
              <div style={{ padding: '20px', textAlign: 'center', color: 'var(--accent-red)' }}>Failed to load stats.</div>
            )}
          </div>
        </div>
      )}

      {/* Edit Player Modal */}
      {editingPlayer && (
        <div style={styles.modalOverlay} onClick={() => setEditingPlayer(null)}>
          <div style={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <div style={styles.modalHeader}>
              <h2>Edit Player Profile</h2>
              <button style={styles.closeBtn} onClick={() => setEditingPlayer(null)}>×</button>
            </div>
            {formError && <div style={styles.error}>{formError}</div>}
            <form onSubmit={handleUpdatePlayer}>
              <div className="form-group">
                <label>Player Name *</label>
                <input
                  type="text"
                  className="form-control"
                  value={editName}
                  onChange={(e) => setEditName(e.target.value)}
                  required
                />
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div className="form-group">
                  <label>Batting Style</label>
                  <select className="form-control" value={editBattingStyle} onChange={(e) => setEditBattingStyle(e.target.value)} style={styles.select}>
                    <option value="RIGHT_HAND">Right Hand Batsman</option>
                    <option value="LEFT_HAND">Left Hand Batsman</option>
                  </select>
                </div>
                <div className="form-group">
                  <label>Bowling Style</label>
                  <select className="form-control" value={editBowlingStyle} onChange={(e) => setEditBowlingStyle(e.target.value)} style={styles.select}>
                    <option value="RIGHT_ARM_FAST">Right Arm Fast</option>
                    <option value="RIGHT_ARM_SPIN">Right Arm Off-Spin</option>
                    <option value="LEFT_ARM_FAST">Left Arm Fast</option>
                    <option value="LEFT_ARM_SPIN">Left Arm Spin</option>
                    <option value="NONE">None</option>
                  </select>
                </div>
              </div>
              <div className="form-group">
                <label>Profile Picture URL</label>
                <input
                  type="url"
                  className="form-control"
                  value={editProfilePic}
                  onChange={(e) => setEditProfilePic(e.target.value)}
                  placeholder="https://example.com/pic.jpg"
                />
              </div>
              <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
                Save Changes
              </button>
            </form>
          </div>
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
    gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
    gap: '24px',
  },
  playerCard: {
    marginBottom: 0,
    background: 'rgba(30, 41, 59, 0.4)',
  },
  playerInfo: {
    display: 'flex',
    alignItems: 'center',
    gap: '16px',
  },
  avatar: {
    width: '60px',
    height: '60px',
    borderRadius: '50%',
    background: 'rgba(16, 185, 129, 0.2)',
    color: 'var(--accent-green)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontWeight: '700',
    fontSize: '1.5rem',
    overflow: 'hidden',
    border: '1px solid var(--accent-green)',
  },
  avatarImg: {
    width: '100%',
    height: '100%',
    objectFit: 'cover',
  },
  styleBadges: {
    display: 'flex',
    flexDirection: 'column',
    gap: '4px',
    marginTop: '4px',
  },
  styleBadge: {
    fontSize: '0.75rem',
    color: 'var(--text-muted)',
    background: 'rgba(255, 255, 255, 0.05)',
    padding: '2px 6px',
    borderRadius: '4px',
    width: 'fit-content',
  },
  modalOverlay: {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    background: 'rgba(0, 0, 0, 0.75)',
    backdropFilter: 'blur(4px)',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 1000,
    padding: '20px',
  },
  modalContent: {
    background: 'var(--bg-secondary)',
    border: '1px solid var(--border-glass)',
    borderRadius: '16px',
    width: '100%',
    maxWidth: '650px',
    padding: '28px',
    boxShadow: '0 10px 50px rgba(0,0,0,0.6)',
    maxHeight: '90vh',
    overflowY: 'auto',
  },
  modalHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '20px',
    borderBottom: '1px solid var(--border-glass)',
    paddingBottom: '12px',
  },
  closeBtn: {
    background: 'none',
    border: 'none',
    color: 'var(--text-muted)',
    fontSize: '2rem',
    cursor: 'pointer',
    lineHeight: '1',
  },
  error: {
    padding: '10px',
    background: 'rgba(239, 68, 68, 0.1)',
    color: 'var(--accent-red)',
    borderRadius: '6px',
    marginBottom: '16px',
    fontSize: '0.9rem',
  },
  select: {
    background: '#1f2937',
    color: 'white',
  },
  statsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(4, 1fr)',
    gap: '12px',
  },
  statBox: {
    background: 'rgba(255, 255, 255, 0.02)',
    border: '1px solid var(--border-glass)',
    borderRadius: '8px',
    padding: '10px 4px',
    textAlign: 'center',
    display: 'flex',
    flexDirection: 'column',
    gap: '4px',
  },
  statVal: {
    fontSize: '1.25rem',
    fontWeight: '700',
    color: '#ffffff',
  },
  statLabel: {
    fontSize: '0.75rem',
    color: 'var(--text-muted)',
    textTransform: 'uppercase',
  }
};

export default Players;
