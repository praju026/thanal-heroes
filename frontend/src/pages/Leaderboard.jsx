import React, { useState, useEffect, useContext } from 'react';
import api from '../services/api';
import { AuthContext } from '../context/AuthContext';

const Leaderboard = () => {
  const { user } = useContext(AuthContext);
  const [activeTab, setActiveTab] = useState('batting');
  const [battingList, setBattingList] = useState([]);
  const [bowlingList, setBowlingList] = useState([]);
  const [loading, setLoading] = useState(false);
  const [refreshing, setRefreshing] = useState(false);

  const fetchLeaderboards = async () => {
    setLoading(true);
    try {
      const batRes = await api.get('/api/v1/leaderboards/batting');
      setBattingList(batRes.data || []);

      const bowlRes = await api.get('/api/v1/leaderboards/bowling');
      setBowlingList(bowlRes.data || []);
    } catch (err) {
      console.error('Failed to load leaderboards data', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLeaderboards();
  }, []);

  const handleForceRefresh = async () => {
    setRefreshing(true);
    try {
      await api.post('/api/v1/leaderboards/refresh');
      alert('Leaderboards recalculated successfully!');
      fetchLeaderboards();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to refresh statistics');
    } finally {
      setRefreshing(false);
    }
  };

  const isAdmin = user && user.role === 'ADMIN';

  return (
    <div style={{ padding: '24px 32px', maxWidth: '1000px', margin: '0 auto', width: '100%' }}>
      <div style={styles.header}>
        <div>
          <h1>Platform Leaderboards</h1>
          <p style={{ color: 'var(--text-muted)' }}>Top performers across all matches (updated automatically)</p>
        </div>
        {isAdmin && (
          <button className="btn btn-primary" onClick={handleForceRefresh} disabled={refreshing}>
            {refreshing ? 'Recalculating...' : '🔄 Recalculate Stats'}
          </button>
        )}
      </div>

      {/* Tabs */}
      <div style={styles.tabBar}>
        <button
          style={{
            ...styles.tabBtn,
            color: activeTab === 'batting' ? 'var(--accent-gold)' : 'var(--text-muted)',
            borderBottom: activeTab === 'batting' ? '3px solid var(--accent-gold)' : 'none'
          }}
          onClick={() => setActiveTab('batting')}
        >
          🍊 ORANGE CAP (Most Runs)
        </button>
        <button
          style={{
            ...styles.tabBtn,
            color: activeTab === 'bowling' ? '#a78bfa' : 'var(--text-muted)',
            borderBottom: activeTab === 'bowling' ? '3px solid #a78bfa' : 'none'
          }}
          onClick={() => setActiveTab('bowling')}
        >
          💜 PURPLE CAP (Most Wickets)
        </button>
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px' }}>Loading leaderboards...</div>
      ) : activeTab === 'batting' ? (
        /* Orange Cap Table */
        <div className="card orange-cap" style={{ background: 'rgba(245, 158, 11, 0.02)' }}>
          <h2 style={{ color: 'var(--accent-gold)', marginBottom: '8px' }}>Top Run Scorers</h2>
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Rank</th>
                  <th>Player Name</th>
                  <th>Matches</th>
                  <th>Innings</th>
                  <th>Runs</th>
                  <th>Highest Score</th>
                  <th>Average</th>
                  <th>Strike Rate</th>
                  <th>50s/100s</th>
                </tr>
              </thead>
              <tbody>
                {battingList.length === 0 ? (
                  <tr>
                    <td colSpan={9} style={{ textAlign: 'center', color: 'var(--text-muted)' }}>
                      No stats recorded yet. Play matches to populate the board!
                    </td>
                  </tr>
                ) : (
                  battingList.map((player, idx) => (
                    <tr key={player.playerId}>
                      <td style={{ fontWeight: '800', color: idx === 0 ? 'var(--accent-gold)' : 'var(--text-muted)' }}>
                        {idx + 1} {idx === 0 ? '👑' : ''}
                      </td>
                      <td style={{ fontWeight: '700' }}>{player.playerName}</td>
                      <td>{player.matchesPlayed}</td>
                      <td>{player.inningsPlayed}</td>
                      <td style={{ fontWeight: '800', color: 'var(--accent-gold)' }}>{player.totalRuns}</td>
                      <td>{player.highestScore}</td>
                      <td>{player.average}</td>
                      <td>{player.strikeRate}%</td>
                      <td>{player.fifties} / {player.hundreds}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      ) : (
        /* Purple Cap Table */
        <div className="card purple-cap" style={{ background: 'rgba(139, 92, 246, 0.02)' }}>
          <h2 style={{ color: '#a78bfa', marginBottom: '8px' }}>Top Wicket Takers</h2>
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Rank</th>
                  <th>Player Name</th>
                  <th>Matches</th>
                  <th>Innings</th>
                  <th>Overs</th>
                  <th>Runs Conceded</th>
                  <th>Wickets</th>
                  <th>Best Bowling</th>
                  <th>Economy</th>
                  <th>Average</th>
                </tr>
              </thead>
              <tbody>
                {bowlingList.length === 0 ? (
                  <tr>
                    <td colSpan={10} style={{ textAlign: 'center', color: 'var(--text-muted)' }}>
                      No stats recorded yet. Play matches to populate the board!
                    </td>
                  </tr>
                ) : (
                  bowlingList.map((player, idx) => (
                    <tr key={player.playerId}>
                      <td style={{ fontWeight: '800', color: idx === 0 ? '#a78bfa' : 'var(--text-muted)' }}>
                        {idx + 1} {idx === 0 ? '👑' : ''}
                      </td>
                      <td style={{ fontWeight: '700' }}>{player.playerName}</td>
                      <td>{player.matchesPlayed}</td>
                      <td>{player.inningsBowled}</td>
                      <td>{player.oversBowled}</td>
                      <td>{player.runsConceded}</td>
                      <td style={{ fontWeight: '800', color: '#a78bfa' }}>{player.wickets}</td>
                      <td>{player.bestBowling}</td>
                      <td>{player.economyRate}</td>
                      <td>{player.average}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
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
    marginBottom: '32px',
  },
  tabBar: {
    display: 'flex',
    gap: '24px',
    borderBottom: '1px solid var(--border-glass)',
    marginBottom: '24px',
  },
  tabBtn: {
    background: 'none',
    border: 'none',
    padding: '12px 16px',
    fontSize: '1rem',
    fontWeight: '700',
    cursor: 'pointer',
    transition: 'all 0.2s ease',
  }
};

export default Leaderboard;
