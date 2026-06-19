import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';
import api from '../services/api';

const MatchDetails = () => {
  const { id } = useParams();
  const [match, setMatch] = useState(null);
  const [loading, setLoading] = useState(true);
  const [flash, setFlash] = useState(false);

  const fetchMatchDetails = async () => {
    try {
      const response = await api.get(`/api/v1/matches/${id}`);
      setMatch(response.data);
    } catch (err) {
      console.error('Failed to fetch match details', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMatchDetails();

    // Setup Socket connection
    const backendUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
    const socket = new SockJS(`${backendUrl}/ws`);
    const stompClient = Stomp.over(socket);
    
    // Silence stomp debug logging
    stompClient.debug = () => {};

    stompClient.connect({}, () => {
      stompClient.subscribe(`/topic/live-match/${id}`, (message) => {
        if (message.body) {
          const updatedMatch = JSON.parse(message.body);
          setMatch(updatedMatch);
          setFlash(true);
          setTimeout(() => setFlash(false), 1000);
        }
      });
    }, (err) => {
      console.error('WebSocket connection failed:', err);
    });

    return () => {
      if (stompClient && stompClient.connected) {
        stompClient.disconnect();
      }
    };
  }, [id]);

  if (loading) {
    return <div style={{ textAlign: 'center', padding: '40px' }}>Loading match scorecard...</div>;
  }

  if (!match) {
    return (
      <div style={{ textAlign: 'center', padding: '40px' }}>
        <h2>Match not found</h2>
        <Link to="/" className="btn btn-secondary" style={{ marginTop: '16px' }}>Back to Dashboard</Link>
      </div>
    );
  }

  return (
    <div style={{ padding: '24px 32px', maxWidth: '900px', margin: '0 auto', width: '100%' }}>
      <div style={{ marginBottom: '16px' }}>
        <Link to="/" style={{ color: 'var(--accent-green)', textDecoration: 'none', fontWeight: 600 }}>
          ← Back to Dashboard
        </Link>
      </div>

      {/* Main Score Board Header */}
      <div className={`card ${flash ? 'flash-update' : ''}`} style={styles.scoreboard}>
        <div style={styles.headerRow}>
          <span style={styles.tournamentName}>🏏 Local Cricket Championship</span>
          <span className="badge badge-live" style={styles.liveIndicator}>
            {match.status === 'IN_PROGRESS' ? 'LIVE SCORE' : match.status}
          </span>
        </div>

        <div style={styles.teamsGrid}>
          {/* Team 1 */}
          <div style={styles.teamSection}>
            <h2 style={styles.teamTitle}>{match.team1Name}</h2>
            {match.innings && match.innings.find(i => i.battingTeamId === match.team1Id) ? (
              <div style={styles.scoreRow}>
                <span style={styles.runsText}>
                  {match.innings.find(i => i.battingTeamId === match.team1Id).totalRuns}/
                  {match.innings.find(i => i.battingTeamId === match.team1Id).totalWickets}
                </span>
                <span style={styles.oversText}>
                  ({match.innings.find(i => i.battingTeamId === match.team1Id).totalOvers} ov)
                </span>
              </div>
            ) : (
              <div style={styles.noScore}>Yet to bat</div>
            )}
          </div>

          <div style={styles.vsDivider}>VS</div>

          {/* Team 2 */}
          <div style={styles.teamSection}>
            <h2 style={styles.teamTitle}>{match.team2Name}</h2>
            {match.innings && match.innings.find(i => i.battingTeamId === match.team2Id) ? (
              <div style={styles.scoreRow}>
                <span style={styles.runsText}>
                  {match.innings.find(i => i.battingTeamId === match.team2Id).totalRuns}/
                  {match.innings.find(i => i.battingTeamId === match.team2Id).totalWickets}
                </span>
                <span style={styles.oversText}>
                  ({match.innings.find(i => i.battingTeamId === match.team2Id).totalOvers} ov)
                </span>
              </div>
            ) : (
              <div style={styles.noScore}>Yet to bat</div>
            )}
          </div>
        </div>

        {match.tossWinnerName && (
          <div style={styles.tossInfo}>
            🗣️ Toss: <strong>{match.tossWinnerName}</strong> won & elected to <strong>{match.tossDecision}</strong>
          </div>
        )}

        {match.resultMarginDetail && (
          <div style={styles.resultText}>🏆 {match.resultMarginDetail}</div>
        )}
      </div>

      {/* Innings scorecard tables */}
      <h2>Innings Breakdown</h2>
      {match.innings && match.innings.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', color: 'var(--text-muted)' }}>
          No innings started yet. Roster and toss updates pending.
        </div>
      ) : (
        match.innings.map((inn) => (
          <div key={inn.id} className="card" style={{ borderLeft: '4px solid var(--accent-green)' }}>
            <div style={styles.innHeader}>
              <h3>Innings {inn.inningsNumber} - {inn.battingTeamName}</h3>
              {inn.completed ? (
                <span className="badge badge-completed">Completed</span>
              ) : (
                <span className="badge badge-live">In Progress</span>
              )}
            </div>

            <div style={styles.innStatsGrid}>
              <div style={styles.innStat}>
                <span style={styles.innVal}>{inn.totalRuns}</span>
                <span style={styles.innLabel}>Total Runs</span>
              </div>
              <div style={styles.innStat}>
                <span style={styles.innVal}>{inn.totalWickets}</span>
                <span style={styles.innLabel}>Wickets Lost</span>
              </div>
              <div style={styles.innStat}>
                <span style={styles.innVal}>{inn.totalOvers}</span>
                <span style={styles.innLabel}>Overs Bowled</span>
              </div>
              <div style={styles.innStat}>
                <span style={styles.innVal}>
                  {inn.totalOvers > 0 
                    ? (inn.totalRuns / parseFloat(inn.totalOvers)).toFixed(2)
                    : '0.00'
                  }
                </span>
                <span style={styles.innLabel}>Run Rate (RPO)</span>
              </div>
            </div>
          </div>
        ))
      )}
    </div>
  );
};

const styles = {
  scoreboard: {
    background: 'linear-gradient(135deg, rgba(15, 23, 42, 0.95), rgba(30, 41, 59, 0.95))',
    border: '1px solid rgba(16, 185, 129, 0.25)',
    boxShadow: 'var(--shadow-glow)',
    padding: '32px',
    textAlign: 'center',
    marginBottom: '32px',
  },
  headerRow: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    borderBottom: '1px solid var(--border-glass)',
    paddingBottom: '16px',
    marginBottom: '24px',
  },
  tournamentName: {
    fontSize: '0.9rem',
    color: 'var(--accent-gold)',
    fontWeight: '700',
    textTransform: 'uppercase',
  },
  liveIndicator: {
    fontSize: '0.8rem',
    letterSpacing: '0.05em',
  },
  teamsGrid: {
    display: 'flex',
    justifyContent: 'space-around',
    alignItems: 'center',
    margin: '24px 0',
  },
  teamSection: {
    flex: 1,
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },
  teamTitle: {
    fontSize: '1.6rem',
    fontWeight: '800',
    marginBottom: '8px',
  },
  scoreRow: {
    display: 'flex',
    alignItems: 'baseline',
    gap: '8px',
  },
  runsText: {
    fontSize: '2.5rem',
    fontWeight: '800',
    color: '#ffffff',
  },
  oversText: {
    fontSize: '1.2rem',
    color: 'var(--text-muted)',
  },
  noScore: {
    fontSize: '1.1rem',
    color: 'var(--text-muted)',
    fontStyle: 'italic',
  },
  vsDivider: {
    fontSize: '1.1rem',
    fontWeight: '800',
    padding: '8px 14px',
    background: 'rgba(255, 255, 255, 0.05)',
    border: '1px solid var(--border-glass)',
    borderRadius: '50%',
    color: 'var(--accent-gold)',
    margin: '0 20px',
  },
  tossInfo: {
    fontSize: '0.95rem',
    color: 'var(--text-muted)',
    padding: '8px',
    background: 'rgba(255, 255, 255, 0.03)',
    borderRadius: '8px',
    display: 'inline-block',
    marginTop: '16px',
  },
  resultText: {
    marginTop: '16px',
    fontSize: '1.2rem',
    fontWeight: '700',
    color: 'var(--accent-gold)',
    background: 'rgba(245, 158, 11, 0.08)',
    padding: '8px 16px',
    borderRadius: '8px',
    display: 'inline-block',
    border: '1px solid rgba(245, 158, 11, 0.2)',
  },
  innHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '16px',
    borderBottom: '1px solid var(--border-glass)',
    paddingBottom: '8px',
  },
  innStatsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(4, 1fr)',
    gap: '16px',
  },
  innStat: {
    textAlign: 'center',
    background: 'rgba(255, 255, 255, 0.01)',
    padding: '12px 8px',
    borderRadius: '8px',
    border: '1px solid var(--border-glass)',
  },
  innVal: {
    display: 'block',
    fontSize: '1.5rem',
    fontWeight: '700',
    color: '#ffffff',
  },
  innLabel: {
    display: 'block',
    fontSize: '0.75rem',
    color: 'var(--text-muted)',
    textTransform: 'uppercase',
    marginTop: '4px',
  }
};

export default MatchDetails;
