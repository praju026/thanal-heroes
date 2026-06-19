import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import api from '../services/api';

const ScorerConsole = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [match, setMatch] = useState(null);
  const [loading, setLoading] = useState(true);
  const [activeInnings, setActiveInnings] = useState(null);

  // Lists for dropdowns
  const [team1Players, setTeam1Players] = useState([]);
  const [team2Players, setTeam2Players] = useState([]);

  // 1. Toss State
  const [tossWinner, setTossWinner] = useState('');
  const [tossDecision, setTossDecision] = useState('BAT');

  // 2. Squad State
  const [team1Squad, setTeam1Squad] = useState([]);
  const [team2Squad, setTeam2Squad] = useState([]);

  // 3. Innings State
  const [battingTeam, setBattingTeam] = useState('');
  const [bowlingTeam, setBowlingTeam] = useState('');
  const [inningsNo, setInningsNo] = useState(1);

  // 4. Scoring Event State
  const [batsmanId, setBatsmanId] = useState('');
  const [nonStrikerId, setNonStrikerId] = useState('');
  const [bowlerId, setBowlerId] = useState('');
  const [runsOffBat, setRunsOffBat] = useState(0);
  const [extraRuns, setExtraRuns] = useState(0);
  const [extraType, setExtraType] = useState('NONE');
  const [isWicket, setIsWicket] = useState(false);
  const [dismissalType, setDismissalType] = useState('NONE');
  const [fielderId, setFielderId] = useState('');

  // 5. Complete Match State
  const [matchWinner, setMatchWinner] = useState('');
  const [resultDetail, setResultDetail] = useState('');

  const fetchMatch = async () => {
    try {
      const response = await api.get(`/api/v1/matches/${id}`);
      setMatch(response.data);

      // Set active innings if in progress
      if (response.data.innings && response.data.innings.length > 0) {
        const active = response.data.innings.find(i => !i.completed);
        setActiveInnings(active || null);
        if (active) {
          setInningsNo(active.inningsNumber);
        }
      }

      // Fetch squad lists for team 1 & 2
      const t1Res = await api.get(`/api/v1/teams/${response.data.team1Id}`);
      setTeam1Players(t1Res.data.players || []);

      const t2Res = await api.get(`/api/v1/teams/${response.data.team2Id}`);
      setTeam2Players(t2Res.data.players || []);

      // Pre-fill squads if already saved
      const mPlayersRes = await api.get(`/api/v1/matches/${id}`);
      // Try to read match squads from backend if matches API returned details
    } catch (err) {
      console.error('Error loading scorer console details', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMatch();
  }, [id]);

  const handleRecordToss = async (e) => {
    e.preventDefault();
    if (!tossWinner) return;
    try {
      const response = await api.put(`/api/v1/matches/${id}/toss?tossWinnerId=${tossWinner}&tossDecision=${tossDecision}`);
      setMatch(response.data);
      fetchMatch();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to record toss');
    }
  };

  const handleSaveSquads = async (e) => {
    e.preventDefault();
    try {
      // Save Team 1 Playing XI
      await api.post(`/api/v1/matches/${id}/players?teamId=${match.team1Id}`, {
        playerIds: team1Players.map(p => p.id),
        playingXiIds: team1Squad.length > 0 ? team1Squad : team1Players.map(p => p.id)
      });

      // Save Team 2 Playing XI
      await api.post(`/api/v1/matches/${id}/players?teamId=${match.team2Id}`, {
        playerIds: team2Players.map(p => p.id),
        playingXiIds: team2Squad.length > 0 ? team2Squad : team2Players.map(p => p.id)
      });

      alert('Squad assignments saved successfully!');
      // Transition status to next
      fetchMatch();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to save squads');
    }
  };

  const handleStartInnings = async (e) => {
    e.preventDefault();
    if (!battingTeam || !bowlingTeam) return;
    try {
      const response = await api.post(`/api/v1/matches/innings?matchId=${id}&battingTeamId=${battingTeam}&bowlingTeamId=${bowlingTeam}&inningsNumber=${inningsNo}`);
      setActiveInnings(response.data);
      fetchMatch();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to start innings');
    }
  };

  const handleRecordBall = async (e) => {
    e.preventDefault();
    if (!activeInnings) return;
    try {
      await api.post('/api/v1/matches/score-event', {
        inningsId: activeInnings.id,
        overNumber: Math.floor(parseFloat(activeInnings.totalOvers)),
        ballNumber: Math.round((parseFloat(activeInnings.totalOvers) % 1) * 10) + 1,
        batsmanId,
        nonStrikerId,
        bowlerId,
        runsOffBat: parseInt(runsOffBat),
        extraRuns: parseInt(extraRuns),
        extraType,
        wicket: isWicket,
        dismissalType,
        fielderId: fielderId || null
      });

      // Reset ball inputs
      setRunsOffBat(0);
      setExtraRuns(0);
      setExtraType('NONE');
      setIsWicket(false);
      setDismissalType('NONE');
      setFielderId('');

      // Refresh Match scorecard
      fetchMatch();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to record ball event');
    }
  };

  const handleCompleteInnings = async () => {
    if (!activeInnings) return;
    if (!window.confirm('Are you sure you want to end this innings?')) return;
    try {
      await api.put(`/api/v1/matches/innings/${activeInnings.id}/complete`);
      setActiveInnings(null);
      fetchMatch();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to end innings');
    }
  };

  const handleCompleteMatch = async (e) => {
    e.preventDefault();
    if (!matchWinner || !resultDetail) return;
    try {
      await api.put(`/api/v1/matches/${id}/complete?winnerId=${matchWinner}&resultMarginDetail=${resultDetail}`);
      navigate(`/matches/${id}`);
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to complete match');
    }
  };

  const handleToggleSquad = (team, playerId) => {
    if (team === 1) {
      if (team1Squad.includes(playerId)) {
        setTeam1Squad(team1Squad.filter(id => id !== playerId));
      } else {
        setTeam1Squad([...team1Squad, playerId]);
      }
    } else {
      if (team2Squad.includes(playerId)) {
        setTeam2Squad(team2Squad.filter(id => id !== playerId));
      } else {
        setTeam2Squad([...team2Squad, playerId]);
      }
    }
  };

  if (loading) return <div style={{ padding: '40px', textAlign: 'center' }}>Loading scorer console...</div>;
  if (!match) return <div style={{ padding: '40px', textAlign: 'center' }}>Match not found.</div>;

  const currentBattingSquad = activeInnings 
    ? (activeInnings.battingTeamId === match.team1Id ? team1Players : team2Players)
    : [];

  const currentBowlingSquad = activeInnings
    ? (activeInnings.bowlingTeamId === match.team1Id ? team1Players : team2Players)
    : [];

  return (
    <div style={{ padding: '24px 32px', maxWidth: '1000px', margin: '0 auto', width: '100%' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <h1>Scoring Cockpit</h1>
        <Link to={`/matches/${id}`} className="btn btn-secondary">Spectator View</Link>
      </div>

      {/* Match Snapshot Header */}
      <div className="card" style={{ background: 'rgba(30, 41, 59, 0.5)' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
          <h3>{match.team1Name} vs {match.team2Name}</h3>
          <span className="badge badge-live">{match.status}</span>
        </div>
        {activeInnings && (
          <h2 style={{ color: 'var(--accent-green)', marginTop: '8px' }}>
            Current score: {activeInnings.totalRuns}/{activeInnings.totalWickets} ({activeInnings.totalOvers} ov)
          </h2>
        )}
      </div>

      {/* STEP 1: RECORD TOSS */}
      {match.status === 'SCHEDULED' && (
        <div className="card">
          <h2>Step 1: Record Toss Details</h2>
          <form onSubmit={handleRecordToss} style={{ marginTop: '16px' }}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
              <div className="form-group">
                <label>Toss Winner</label>
                <select className="form-control" value={tossWinner} onChange={(e) => setTossWinner(e.target.value)} required style={styles.select}>
                  <option value="">Select team...</option>
                  <option value={match.team1Id}>{match.team1Name}</option>
                  <option value={match.team2Id}>{match.team2Name}</option>
                </select>
              </div>
              <div className="form-group">
                <label>Decision</label>
                <select className="form-control" value={tossDecision} onChange={(e) => setTossDecision(e.target.value)} required style={styles.select}>
                  <option value="BAT">Bat First</option>
                  <option value="BOWL">Bowl First</option>
                </select>
              </div>
            </div>
            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
              Submit Toss Results
            </button>
          </form>
        </div>
      )}

      {/* STEP 2: SETUP PLAYING XI */}
      {match.status === 'TOSS_PENDING' && (
        <div className="card">
          <h2>Step 2: Assign Squad Rosters / Playing XI</h2>
          <p style={{ color: 'var(--text-muted)', marginBottom: '16px' }}>Select the players representing each team in this match.</p>

          <form onSubmit={handleSaveSquads}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
              <div>
                <h3>{match.team1Name} Squad</h3>
                <div style={styles.squadSelectBox}>
                  {team1Players.map(p => (
                    <label key={p.id} style={styles.checkboxLabel}>
                      <input
                        type="checkbox"
                        checked={team1Squad.includes(p.id)}
                        onChange={() => handleToggleSquad(1, p.id)}
                      />
                      <span>{p.name} ({p.battingStyle?.replace('_', ' ')})</span>
                    </label>
                  ))}
                </div>
              </div>
              <div>
                <h3>{match.team2Name} Squad</h3>
                <div style={styles.squadSelectBox}>
                  {team2Players.map(p => (
                    <label key={p.id} style={styles.checkboxLabel}>
                      <input
                        type="checkbox"
                        checked={team2Squad.includes(p.id)}
                        onChange={() => handleToggleSquad(2, p.id)}
                      />
                      <span>{p.name} ({p.battingStyle?.replace('_', ' ')})</span>
                    </label>
                  ))}
                </div>
              </div>
            </div>
            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '24px' }}>
              Save Playing Squads
            </button>
          </form>
        </div>
      )}

      {/* STEP 3: START INNINGS */}
      {match.status === 'INNINGS_BREAK' || (match.status === 'TOSS_PENDING' && match.tossWinnerId != null) || (match.status === 'TOSS_PENDING' && !activeInnings && match.tossDecision != null) ? (
        <div className="card">
          <h2>Step 3: Start Innings</h2>
          <form onSubmit={handleStartInnings} style={{ marginTop: '16px' }}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px' }}>
              <div className="form-group">
                <label>Batting Team</label>
                <select className="form-control" value={battingTeam} onChange={(e) => setBattingTeam(e.target.value)} required style={styles.select}>
                  <option value="">Select...</option>
                  <option value={match.team1Id}>{match.team1Name}</option>
                  <option value={match.team2Id}>{match.team2Name}</option>
                </select>
              </div>
              <div className="form-group">
                <label>Bowling Team</label>
                <select className="form-control" value={bowlingTeam} onChange={(e) => setBowlingTeam(e.target.value)} required style={styles.select}>
                  <option value="">Select...</option>
                  <option value={match.team1Id}>{match.team1Name}</option>
                  <option value={match.team2Id}>{match.team2Name}</option>
                </select>
              </div>
              <div className="form-group">
                <label>Innings Number</label>
                <select className="form-control" value={inningsNo} onChange={(e) => setInningsNo(parseInt(e.target.value))} style={styles.select}>
                  <option value={1}>Innings 1</option>
                  <option value={2}>Innings 2</option>
                </select>
              </div>
            </div>
            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
              Start Innings
            </button>
          </form>
        </div>
      ) : null}

      {/* STEP 4: RECORD BALL BY BALL */}
      {match.status === 'IN_PROGRESS' && activeInnings && (
        <div className="card">
          <h2>Step 4: Live Ball-by-Ball Logging</h2>
          <form onSubmit={handleRecordBall} style={{ marginTop: '16px' }}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px', marginBottom: '16px' }}>
              <div className="form-group">
                <label>Batsman on Strike *</label>
                <select className="form-control" value={batsmanId} onChange={(e) => setBatsmanId(e.target.value)} required style={styles.select}>
                  <option value="">Select batsman...</option>
                  {currentBattingSquad.map(p => (
                    <option key={p.id} value={p.id}>{p.name}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Non-Striker *</label>
                <select className="form-control" value={nonStrikerId} onChange={(e) => setNonStrikerId(e.target.value)} required style={styles.select}>
                  <option value="">Select batsman...</option>
                  {currentBattingSquad.map(p => (
                    <option key={p.id} value={p.id}>{p.name}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Bowler *</label>
                <select className="form-control" value={bowlerId} onChange={(e) => setBowlerId(e.target.value)} required style={styles.select}>
                  <option value="">Select bowler...</option>
                  {currentBowlingSquad.map(p => (
                    <option key={p.id} value={p.id}>{p.name}</option>
                  ))}
                </select>
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px', marginBottom: '16px' }}>
              <div className="form-group">
                <label>Runs off Bat</label>
                <select className="form-control" value={runsOffBat} onChange={(e) => setRunsOffBat(parseInt(e.target.value))} style={styles.select}>
                  <option value={0}>0 Runs</option>
                  <option value={1}>1 Run</option>
                  <option value={2}>2 Runs</option>
                  <option value={3}>3 Runs</option>
                  <option value={4}>4 Runs (Boundary)</option>
                  <option value={6}>6 Runs (Sixer)</option>
                </select>
              </div>
              <div className="form-group">
                <label>Extra Runs Conceded</label>
                <input
                  type="number"
                  className="form-control"
                  value={extraRuns}
                  onChange={(e) => setExtraRuns(parseInt(e.target.value))}
                  min={0}
                />
              </div>
              <div className="form-group">
                <label>Extra Type</label>
                <select className="form-control" value={extraType} onChange={(e) => setExtraType(e.target.value)} style={styles.select}>
                  <option value="NONE">None (Legal ball)</option>
                  <option value="WD">Wide (Re-bowl)</option>
                  <option value="NB">No Ball (Re-bowl)</option>
                  <option value="LB">Leg Bye</option>
                  <option value="B">Bye</option>
                  <option value="PENALTY">Penalty</option>
                </select>
              </div>
            </div>

            {/* Wicket Section */}
            <div style={styles.wicketPanel}>
              <label style={styles.checkboxLabel}>
                <input
                  type="checkbox"
                  checked={isWicket}
                  onChange={(e) => setIsWicket(e.target.checked)}
                />
                <span style={{ fontWeight: '700', color: 'var(--accent-red)' }}>🚨 OUT / WICKET EVENT</span>
              </label>

              {isWicket && (
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginTop: '12px' }}>
                  <div className="form-group">
                    <label>Dismissal Type</label>
                    <select className="form-control" value={dismissalType} onChange={(e) => setDismissalType(e.target.value)} style={styles.select} required>
                      <option value="NONE">Select type...</option>
                      <option value="BOWLED">Bowled</option>
                      <option value="CAUGHT">Caught</option>
                      <option value="LBW">LBW</option>
                      <option value="STUMPED">Stumped</option>
                      <option value="RUN_OUT">Run Out</option>
                      <option value="HIT_WICKET">Hit Wicket</option>
                      <option value="RETIRED">Retired Hurt</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label>Fielder (Fielder / Catch taker)</label>
                    <select className="form-control" value={fielderId} onChange={(e) => setFielderId(e.target.value)} style={styles.select}>
                      <option value="">Select fielder...</option>
                      {currentBowlingSquad.map(p => (
                        <option key={p.id} value={p.id}>{p.name}</option>
                      ))}
                    </select>
                  </div>
                </div>
              )}
            </div>

            <div style={{ display: 'flex', gap: '16px', marginTop: '20px' }}>
              <button type="submit" className="btn btn-primary" style={{ flex: 2 }}>
                Record Ball & Update Score
              </button>
              <button type="button" className="btn btn-secondary" onClick={handleCompleteInnings} style={{ flex: 1 }}>
                End Innings
              </button>
            </div>
          </form>
        </div>
      )}

      {/* STEP 5: COMPLETE MATCH */}
      {match.status === 'INNINGS_BREAK' && !activeInnings && (
        <div className="card">
          <h2>Step 5: End Innings / Complete Match</h2>
          <div style={{ display: 'flex', gap: '16px', marginBottom: '24px' }}>
            <button className="btn btn-secondary" onClick={() => {
              setInningsNo(2);
              setBattingTeam(match.team2Id);
              setBowlingTeam(match.team1Id);
              // Focus view to start innings 2
              alert('Configure start innings fields above to launch Innings 2.');
            }}>
              Prepare Innings 2
            </button>
          </div>

          <form onSubmit={handleCompleteMatch} style={styles.completeMatchForm}>
            <h3>Conclude Match & Declare Winner</h3>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginTop: '16px' }}>
              <div className="form-group">
                <label>Winner Team</label>
                <select className="form-control" value={matchWinner} onChange={(e) => setMatchWinner(e.target.value)} required style={styles.select}>
                  <option value="">Select winner...</option>
                  <option value={match.team1Id}>{match.team1Name}</option>
                  <option value={match.team2Id}>{match.team2Name}</option>
                </select>
              </div>
              <div className="form-group">
                <label>Result Summary Detail</label>
                <input
                  type="text"
                  className="form-control"
                  value={resultDetail}
                  onChange={(e) => setResultDetail(e.target.value)}
                  required
                  placeholder="e.g. Titans won by 4 wickets"
                />
              </div>
            </div>
            <button type="submit" className="btn btn-danger" style={{ width: '100%', marginTop: '12px' }}>
              Conclude Match & Publish Results
            </button>
          </form>
        </div>
      )}
    </div>
  );
};

const styles = {
  select: {
    background: '#1f2937',
    color: 'white',
    cursor: 'pointer',
  },
  squadSelectBox: {
    maxHeight: '300px',
    overflowY: 'auto',
    border: '1px solid var(--border-glass)',
    padding: '12px',
    borderRadius: '10px',
    background: 'rgba(255, 255, 255, 0.02)',
    display: 'flex',
    flexDirection: 'column',
    gap: '10px',
  },
  checkboxLabel: {
    display: 'flex',
    alignItems: 'center',
    gap: '10px',
    cursor: 'pointer',
  },
  wicketPanel: {
    padding: '16px',
    background: 'rgba(239, 68, 68, 0.03)',
    border: '1px solid rgba(239, 68, 68, 0.15)',
    borderRadius: '10px',
    marginTop: '16px',
  },
  completeMatchForm: {
    padding: '20px',
    border: '1px solid rgba(245, 158, 11, 0.25)',
    background: 'rgba(245, 158, 11, 0.02)',
    borderRadius: '10px',
  }
};

export default ScorerConsole;
