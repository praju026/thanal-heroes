import React, { useState, useEffect, useContext } from 'react';
import api from '../services/api';
import { AuthContext } from '../context/AuthContext';

const Teams = () => {
  const { user } = useContext(AuthContext);
  const [teams, setTeams] = useState([]);
  const [allPlayers, setAllPlayers] = useState([]);
  const [loading, setLoading] = useState(false);
  
  // Selected Team Details state
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [teamPlayers, setTeamPlayers] = useState([]);
  const [selectedPlayerToAdd, setSelectedPlayerToAdd] = useState('');
  
  // Create Team Form state
  const [showAddForm, setShowAddForm] = useState(false);
  const [newTeamName, setNewTeamName] = useState('');
  const [logoUrl, setLogoUrl] = useState('');
  const [formError, setFormError] = useState('');

  // Edit Team state
  const [editingTeam, setEditingTeam] = useState(null);
  const [editTeamName, setEditTeamName] = useState('');
  const [editLogoUrl, setEditLogoUrl] = useState('');

  const fetchTeams = async () => {
    setLoading(true);
    try {
      const response = await api.get('/api/v1/teams');
      setTeams(response.data || []);
    } catch (err) {
      console.error('Failed to fetch teams', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchAllPlayers = async () => {
    try {
      const response = await api.get('/api/v1/players?size=100');
      setAllPlayers(response.data.content || []);
    } catch (err) {
      console.error('Failed to fetch all players', err);
    }
  };

  useEffect(() => {
    fetchTeams();
    fetchAllPlayers();
  }, []);

  const handleCreateTeam = async (e) => {
    e.preventDefault();
    setFormError('');
    try {
      await api.post('/api/v1/teams', {
        name: newTeamName,
        logoUrl: logoUrl || null
      });
      setNewTeamName('');
      setLogoUrl('');
      setShowAddForm(false);
      fetchTeams();
    } catch (err) {
      setFormError(err.response?.data?.message || 'Failed to create team');
    }
  };

  const handleSelectTeam = async (team) => {
    try {
      const response = await api.get(`/api/v1/teams/${team.id}`);
      setSelectedTeam(response.data);
      setTeamPlayers(response.data.players || []);
    } catch (err) {
      console.error('Failed to fetch team details', err);
    }
  };

  const handleAddPlayerToTeam = async (e) => {
    e.preventDefault();
    if (!selectedPlayerToAdd) return;
    try {
      await api.post(`/api/v1/teams/${selectedTeam.id}/players`, {
        playerId: selectedPlayerToAdd
      });
      setSelectedPlayerToAdd('');
      // Refresh current team details
      handleSelectTeam(selectedTeam);
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to add player to team');
    }
  };

  const handleRemovePlayerFromTeam = async (playerId) => {
    if (!window.confirm('Are you sure you want to remove this player from the team?')) return;
    try {
      await api.delete(`/api/v1/teams/${selectedTeam.id}/players/${playerId}`);
      // Refresh current team details
      handleSelectTeam(selectedTeam);
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to remove player');
    }
  };

  const handleStartEditTeam = (team) => {
    setEditingTeam(team);
    setEditTeamName(team.name);
    setEditLogoUrl(team.logoUrl || '');
    setFormError('');
  };

  const handleUpdateTeam = async (e) => {
    e.preventDefault();
    setFormError('');
    try {
      const response = await api.put(`/api/v1/teams/${editingTeam.id}`, {
        name: editTeamName,
        logoUrl: editLogoUrl || null
      });
      setEditingTeam(null);
      fetchTeams();
      setSelectedTeam(response.data);
    } catch (err) {
      setFormError(err.response?.data?.message || 'Failed to update team');
    }
  };

  const handleDeleteTeam = async (id) => {
    if (window.confirm('Are you sure you want to delete this team? This will soft-delete the team.')) {
      try {
        await api.delete(`/api/v1/teams/${id}`);
        setSelectedTeam(null);
        setTeamPlayers([]);
        fetchTeams();
      } catch (err) {
        alert(err.response?.data?.message || 'Failed to delete team');
      }
    }
  };

  const isScorerOrAdmin = user && (user.role === 'ADMIN' || user.role === 'SCORER');

  return (
    <div style={{ padding: '24px 32px', maxWidth: '1200px', margin: '0 auto', width: '100%' }}>
      <div style={styles.header}>
        <h1>Teams & Rosters</h1>
        {isScorerOrAdmin && (
          <button className="btn btn-primary" onClick={() => setShowAddForm(!showAddForm)}>
            {showAddForm ? 'Close Form' : 'Register New Team'}
          </button>
        )}
      </div>

      {showAddForm && (
        <div className="card" style={{ maxWidth: '600px', margin: '0 auto 24px auto' }}>
          <h3>Register Team</h3>
          {formError && <div style={styles.error}>{formError}</div>}
          <form onSubmit={handleCreateTeam}>
            <div className="form-group">
              <label>Team Name *</label>
              <input
                type="text"
                className="form-control"
                value={newTeamName}
                onChange={(e) => setNewTeamName(e.target.value)}
                required
                placeholder="e.g. Thanal Titans"
              />
            </div>
            <div className="form-group">
              <label>Team Logo URL</label>
              <input
                type="url"
                className="form-control"
                value={logoUrl}
                onChange={(e) => setLogoUrl(e.target.value)}
                placeholder="https://example.com/logo.png"
              />
            </div>
            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
              Create Team
            </button>
          </form>
        </div>
      )}

      <div style={styles.splitLayout}>
        {/* Left Side: Teams list */}
        <div style={styles.leftColumn}>
          <h2>Team List</h2>
          {loading ? (
            <div>Loading teams...</div>
          ) : teams.length === 0 ? (
            <div style={{ color: 'var(--text-muted)' }}>No teams registered.</div>
          ) : (
            <div style={styles.teamList}>
              {teams.map((team) => (
                <div
                  key={team.id}
                  className={`card ${selectedTeam && selectedTeam.id === team.id ? 'active-team' : ''}`}
                  onClick={() => handleSelectTeam(team)}
                  style={{
                    ...styles.teamItemCard,
                    borderColor: selectedTeam && selectedTeam.id === team.id ? 'var(--accent-green)' : 'var(--border-glass)'
                  }}
                >
                  <div style={styles.teamBrand}>
                    <div style={styles.logoContainer}>
                      {team.logoUrl ? (
                        <img src={team.logoUrl} alt={team.name} style={styles.logoImg} />
                      ) : (
                        <span>🏏</span>
                      )}
                    </div>
                    <h3>{team.name}</h3>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Right Side: Roster Details */}
        <div style={styles.rightColumn}>
          {selectedTeam ? (
            <div className="card" style={{ height: '100%', marginBottom: 0 }}>
              <div style={styles.detailHeader}>
                <div style={styles.logoContainerLarge}>
                  {selectedTeam.logoUrl ? (
                    <img src={selectedTeam.logoUrl} alt={selectedTeam.name} style={styles.logoImg} />
                  ) : (
                    <span>🏏</span>
                  )}
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '12px' }}>
                    <h2 style={{ marginBottom: '4px' }}>{selectedTeam.name}</h2>
                    {isScorerOrAdmin && (
                      <div style={{ display: 'flex', gap: '8px' }}>
                        <button
                          className="btn btn-secondary btn-sm"
                          onClick={() => handleStartEditTeam(selectedTeam)}
                          style={{ padding: '6px 12px', fontSize: '0.8rem' }}
                        >
                          ✏️ Edit Team
                        </button>
                        {user && user.role === 'ADMIN' && (
                          <button
                            className="btn btn-danger btn-sm"
                            onClick={() => handleDeleteTeam(selectedTeam.id)}
                            style={{ padding: '6px 12px', fontSize: '0.8rem' }}
                          >
                            🗑️ Delete
                          </button>
                        )}
                      </div>
                    )}
                  </div>
                  <p style={{ color: 'var(--text-muted)' }}>Roster Size: {teamPlayers.length} players</p>
                </div>
              </div>

              {isScorerOrAdmin && (
                <form onSubmit={handleAddPlayerToTeam} style={styles.addPlayerForm}>
                  <h4 style={{ marginBottom: '8px' }}>Assign Player to Team</h4>
                  <div style={{ display: 'flex', gap: '12px' }}>
                    <select
                      className="form-control"
                      value={selectedPlayerToAdd}
                      onChange={(e) => setSelectedPlayerToAdd(e.target.value)}
                      style={styles.select}
                      required
                    >
                      <option value="">Select a player...</option>
                      {allPlayers
                        .filter(p => !teamPlayers.some(tp => tp.id === p.id))
                        .map(p => (
                          <option key={p.id} value={p.id}>{p.name}</option>
                        ))
                      }
                    </select>
                    <button type="submit" className="btn btn-primary">Add Player</button>
                  </div>
                </form>
              )}

              <h3 style={{ marginTop: '24px', marginBottom: '12px' }}>Active Squad</h3>
              {teamPlayers.length === 0 ? (
                <div style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '24px' }}>
                  No players assigned to this team. Add players using the dropdown above.
                </div>
              ) : (
                <div className="table-container">
                  <table>
                    <thead>
                      <tr>
                        <th>Player Name</th>
                        <th>Batting Style</th>
                        <th>Bowling Style</th>
                        {isScorerOrAdmin && <th style={{ textAlign: 'right' }}>Actions</th>}
                      </tr>
                    </thead>
                    <tbody>
                      {teamPlayers.map((player) => (
                        <tr key={player.id}>
                          <td style={{ fontWeight: 600 }}>{player.name}</td>
                          <td>{player.battingStyle?.replace('_', ' ')}</td>
                          <td>{player.bowlingStyle !== 'NONE' ? player.bowlingStyle?.replace('_', ' ') : '—'}</td>
                          {isScorerOrAdmin && (
                            <td style={{ textAlign: 'right' }}>
                              <button
                                className="btn btn-danger"
                                style={{ padding: '4px 10px', fontSize: '0.8rem' }}
                                onClick={() => handleRemovePlayerFromTeam(player.id)}
                              >
                                Remove
                              </button>
                            </td>
                          )}
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          ) : (
            <div className="card" style={styles.placeholderCard}>
              <h3>Select a team from the list to view its active roster.</h3>
            </div>
          )}
        </div>
      </div>

      {/* Edit Team Modal */}
      {editingTeam && (
        <div style={styles.modalOverlay} onClick={() => setEditingTeam(null)}>
          <div style={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <div style={styles.modalHeader}>
              <h2>Edit Team Profile</h2>
              <button style={styles.closeBtn} onClick={() => setEditingTeam(null)}>×</button>
            </div>
            {formError && <div style={styles.error}>{formError}</div>}
            <form onSubmit={handleUpdateTeam}>
              <div className="form-group">
                <label>Team Name *</label>
                <input
                  type="text"
                  className="form-control"
                  value={editTeamName}
                  onChange={(e) => setEditTeamName(e.target.value)}
                  required
                />
              </div>
              <div className="form-group">
                <label>Team Logo URL</label>
                <input
                  type="url"
                  className="form-control"
                  value={editLogoUrl}
                  onChange={(e) => setEditLogoUrl(e.target.value)}
                  placeholder="https://example.com/logo.png"
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
  splitLayout: {
    display: 'grid',
    gridTemplateColumns: '1fr 2fr',
    gap: '32px',
    alignItems: 'start',
  },
  leftColumn: {
    display: 'flex',
    flexDirection: 'column',
    gap: '16px',
  },
  rightColumn: {
    minHeight: '400px',
  },
  teamList: {
    display: 'flex',
    flexDirection: 'column',
    gap: '12px',
  },
  teamItemCard: {
    cursor: 'pointer',
    marginBottom: 0,
    background: 'rgba(30, 41, 59, 0.3)',
  },
  teamBrand: {
    display: 'flex',
    alignItems: 'center',
    gap: '16px',
  },
  logoContainer: {
    width: '40px',
    height: '40px',
    borderRadius: '8px',
    background: 'rgba(255, 255, 255, 0.05)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    overflow: 'hidden',
  },
  logoContainerLarge: {
    width: '70px',
    height: '70px',
    borderRadius: '12px',
    background: 'rgba(255, 255, 255, 0.05)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    overflow: 'hidden',
  },
  logoImg: {
    width: '100%',
    height: '100%',
    objectFit: 'cover',
  },
  detailHeader: {
    display: 'flex',
    alignItems: 'center',
    gap: '20px',
    borderBottom: '1px solid var(--border-glass)',
    paddingBottom: '20px',
    marginBottom: '24px',
  },
  addPlayerForm: {
    padding: '16px',
    background: 'rgba(255, 255, 255, 0.02)',
    border: '1px solid var(--border-glass)',
    borderRadius: '10px',
  },
  select: {
    background: '#1f2937',
    color: 'white',
    cursor: 'pointer',
  },
  placeholderCard: {
    height: '100%',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    color: 'var(--text-muted)',
    borderStyle: 'dashed',
    textAlign: 'center',
    padding: '80px 20px',
  },
  error: {
    padding: '10px',
    background: 'rgba(239, 68, 68, 0.1)',
    color: 'var(--accent-red)',
    borderRadius: '6px',
    marginBottom: '16px',
    fontSize: '0.9rem',
  }
};

export default Teams;
