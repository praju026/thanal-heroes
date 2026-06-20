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

  const [isPlayersSetupCompleted, setIsPlayersSetupCompleted] = useState(false);
  const [lastBowlerId, setLastBowlerId] = useState('');

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

      let active = null;
      // Set active innings if in progress
      if (response.data.innings && response.data.innings.length > 0) {
        active = response.data.innings.find(i => !i.completed);
        setActiveInnings(active || null);
        if (active) {
          setInningsNo(active.inningsNumber);
          const totalOversFloat = parseFloat(active.totalOvers);
          if (totalOversFloat > 0 || active.totalRuns > 0 || active.totalWickets > 0) {
            setIsPlayersSetupCompleted(true);
          }
        }
      }

      // Fetch squad lists for team 1 & 2
      const t1Res = await api.get(`/api/v1/teams/${response.data.team1Id}`);
      setTeam1Players(t1Res.data.players || []);

      const t2Res = await api.get(`/api/v1/teams/${response.data.team2Id}`);
      setTeam2Players(t2Res.data.players || []);

      return active;
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
      setIsPlayersSetupCompleted(false);
      fetchMatch();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to start innings');
    }
  };

  const handleRecordBall = async (e) => {
    e.preventDefault();
    if (!activeInnings) return;
    if (!batsmanId || !nonStrikerId || !bowlerId) {
      alert('Striker, Non-Striker, and Bowler are required fields!');
      return;
    }
    if (batsmanId === nonStrikerId) {
      alert('Striker and Non-Striker must be different players!');
      return;
    }
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

      // Refresh Match scorecard and get updated active innings
      const updatedInnings = await fetchMatch();

      // 1. Calculate run-based strike rotation
      let runRotation = false;
      const runs = parseInt(runsOffBat);
      if (runs % 2 === 1) {
        runRotation = true;
      } else if (extraType === 'LB' || extraType === 'B') {
        if (parseInt(extraRuns) % 2 === 1) {
          runRotation = true;
        }
      }

      // 2. Check if over completed
      let overCompleted = false;
      let newBowlerId = bowlerId;
      if (updatedInnings) {
        const currentOvers = parseFloat(updatedInnings.totalOvers);
        if (currentOvers > 0 && currentOvers % 1 === 0) {
          overCompleted = true;
        }
      }

      // Apply changes to striker and non-striker
      let newStriker = batsmanId;
      let newNonStriker = nonStrikerId;

      if (runRotation) {
        const temp = newStriker;
        newStriker = newNonStriker;
        newNonStriker = temp;
      }

      if (overCompleted) {
        // Swap striker/non-striker at the end of the over
        const temp = newStriker;
        newStriker = newNonStriker;
        newNonStriker = temp;
        // Save the previous bowler to exclude them from the next over
        setLastBowlerId(bowlerId);
        // Reset bowler for the new over
        newBowlerId = '';
      }

      if (isWicket) {
        // Striker is dismissed, clear striker selection so scorer must select new batsman
        setBatsmanId('');
        setNonStrikerId(newNonStriker);
      } else {
        setBatsmanId(newStriker);
        setNonStrikerId(newNonStriker);
      }

      setBowlerId(newBowlerId);

      // Reset ball inputs
      setRunsOffBat(0);
      setExtraRuns(0);
      setExtraType('NONE');
      setIsWicket(false);
      setDismissalType('NONE');
      setFielderId('');
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

  const handleSelectExtraType = (type) => {
    setExtraType(type);
    if (type === 'WD' || type === 'NB') {
      setExtraRuns(1);
    } else {
      setExtraRuns(0);
    }
  };

  if (loading) return <div style={{ padding: '40px', textAlign: 'center' }}>Loading scorer console...</div>;
  if (!match) return <div style={{ padding: '40px', textAlign: 'center' }}>Match not found.</div>;

  // BUG FIX: bowling squad is ALWAYS the opposite of the batting squad.
  // This bypasses any potential issues with activeInnings.bowlingTeamId and is 100% correct.
  const currentBattingSquad = activeInnings 
    ? (activeInnings.battingTeamId === match.team1Id ? team1Players : team2Players)
    : [];

  const currentBowlingSquad = activeInnings
    ? (activeInnings.battingTeamId === match.team1Id ? team2Players : team1Players)
    : [];

  // Compute Run Rate
  const runRate = activeInnings && activeInnings.totalOvers > 0
    ? (activeInnings.totalRuns / parseFloat(activeInnings.totalOvers)).toFixed(2)
    : '0.00';

  // Live Stats for Active Batsmen & Bowler
  const strikerStats = activeInnings?.batsmen?.find(b => b.batsmanId === batsmanId);
  const nonStrikerStats = activeInnings?.batsmen?.find(b => b.batsmanId === nonStrikerId);
  const currentBowlerStats = activeInnings?.bowlers?.find(b => b.bowlerId === bowlerId);

  // Available Next Batsmen (not currently active, and not out)
  const getAvailableNextBatsmen = () => {
    if (!activeInnings) return [];
    const outBatsmenIds = activeInnings.batsmen
      ? activeInnings.batsmen.filter(b => b.isOut).map(b => b.batsmanId)
      : [];
    return currentBattingSquad.filter(p => p.id !== nonStrikerId && p.id !== batsmanId && !outBatsmenIds.includes(p.id));
  };

  // Available Next Bowlers (excluding the last bowler who finished the over)
  const getAvailableNextBowlers = () => {
    return currentBowlingSquad.filter(p => p.id !== lastBowlerId);
  };

  // Overlays checking
  const showWicketOverlay = isPlayersSetupCompleted && !batsmanId && getAvailableNextBatsmen().length > 0;
  const showBowlerOverlay = isPlayersSetupCompleted && batsmanId && !bowlerId;

  return (
    <div style={{ padding: '16px 20px', maxWidth: '800px', margin: '0 auto', width: '100%' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h1 style={{ fontSize: '1.8rem', margin: 0 }}>Scoring Cockpit</h1>
        <Link to={`/matches/${id}`} className="btn btn-secondary" style={{ padding: '8px 12px', fontSize: '0.9rem' }}>Spectator View</Link>
      </div>

      {/* Match Digital Scoreboard Banner */}
      <div className="card" style={styles.scoreboardBanner}>
        <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255,255,255,0.08)', paddingBottom: '10px', marginBottom: '12px' }}>
          <span style={{ fontWeight: '800', color: 'var(--accent-gold)' }}>🏏 LIVE SCORING CONSOLE</span>
          <span className="badge badge-live" style={{ fontSize: '0.75rem' }}>{match.status}</span>
        </div>
        
        <div style={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <h3 style={{ margin: '0 0 4px 0', fontSize: '1.2rem', color: 'var(--text-muted)' }}>{match.team1Name} vs {match.team2Name}</h3>
            {activeInnings && (
              <span style={{ fontSize: '0.85rem', color: 'var(--accent-green)', fontWeight: 'bold' }}>
                Batting: {activeInnings.battingTeamName}
              </span>
            )}
          </div>

          {activeInnings ? (
            <div style={{ textAlign: 'right' }}>
              <div style={{ display: 'flex', alignItems: 'baseline', gap: '6px' }}>
                <span style={styles.digitalScore}>{activeInnings.totalRuns}/{activeInnings.totalWickets}</span>
                <span style={{ fontSize: '1rem', color: 'var(--text-muted)' }}>
                  ({activeInnings.totalOvers} / {match.overs || 20} ov)
                </span>
              </div>
              <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '2px' }}>
                Run Rate: <strong style={{ color: 'white' }}>{runRate}</strong>
              </div>
            </div>
          ) : (
            <div style={{ fontStyle: 'italic', color: 'var(--text-muted)' }}>Match Setup in progress...</div>
          )}
        </div>
      </div>

      {/* STEP 1: RECORD TOSS */}
      {match.status === 'SCHEDULED' && (
        <div className="card" style={styles.sectionCard}>
          <h2>Record Toss Details</h2>
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
            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '16px' }}>
              Submit Toss Results
            </button>
          </form>
        </div>
      )}

      {/* STEP 2: SETUP PLAYING XI */}
      {match.status === 'TOSS_PENDING' && (
        <div className="card" style={styles.sectionCard}>
          <h2>Assign Squad Rosters / Playing XI</h2>
          <p style={{ color: 'var(--text-muted)', marginBottom: '16px' }}>Select the players representing each team in this match.</p>

          <form onSubmit={handleSaveSquads}>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
              <div>
                <h3 style={{ fontSize: '1rem', marginBottom: '8px' }}>{match.team1Name} Squad</h3>
                <div style={styles.squadSelectBox}>
                  {team1Players.map(p => (
                    <label key={p.id} style={styles.checkboxLabel}>
                      <input
                        type="checkbox"
                        checked={team1Squad.includes(p.id)}
                        onChange={() => handleToggleSquad(1, p.id)}
                      />
                      <span style={{ fontSize: '0.85rem' }}>{p.name} ({p.battingStyle?.replace('_', ' ')})</span>
                    </label>
                  ))}
                </div>
              </div>
              <div>
                <h3 style={{ fontSize: '1rem', marginBottom: '8px' }}>{match.team2Name} Squad</h3>
                <div style={styles.squadSelectBox}>
                  {team2Players.map(p => (
                    <label key={p.id} style={styles.checkboxLabel}>
                      <input
                        type="checkbox"
                        checked={team2Squad.includes(p.id)}
                        onChange={() => handleToggleSquad(2, p.id)}
                      />
                      <span style={{ fontSize: '0.85rem' }}>{p.name} ({p.battingStyle?.replace('_', ' ')})</span>
                    </label>
                  ))}
                </div>
              </div>
            </div>
            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '20px' }}>
              Save Playing Squads
            </button>
          </form>
        </div>
      )}

      {/* STEP 3: START INNINGS */}
      {match.status === 'INNINGS_BREAK' || (match.status === 'TOSS_PENDING' && match.tossWinnerId != null) || (match.status === 'TOSS_PENDING' && !activeInnings && match.tossDecision != null) ? (
        <div className="card" style={styles.sectionCard}>
          <h2>Start Innings</h2>
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

      {/* STEP 3.5: SELECT STARTING PLAYERS */}
      {match.status === 'IN_PROGRESS' && activeInnings && !isPlayersSetupCompleted && (
        <div className="card" style={styles.sectionCard}>
          <h2>Select Starting Players</h2>
          <p style={{ color: 'var(--text-muted)', marginBottom: '16px' }}>Select the opening batsmen and bowler to start scoring.</p>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px', marginBottom: '20px' }}>
            <div className="form-group">
              <label>Striker (Batter 1)</label>
              <select className="form-control" value={batsmanId} onChange={(e) => setBatsmanId(e.target.value)} required style={styles.select}>
                <option value="">Select Striker...</option>
                {currentBattingSquad.map(p => (
                  <option key={p.id} value={p.id}>{p.name}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Non-Striker (Batter 2)</label>
              <select className="form-control" value={nonStrikerId} onChange={(e) => setNonStrikerId(e.target.value)} required style={styles.select}>
                <option value="">Select Non-Striker...</option>
                {currentBattingSquad.map(p => (
                  <option key={p.id} value={p.id}>{p.name}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Opening Bowler</label>
              <select className="form-control" value={bowlerId} onChange={(e) => setBowlerId(e.target.value)} required style={styles.select}>
                <option value="">Select Bowler...</option>
                {currentBowlingSquad.map(p => (
                  <option key={p.id} value={p.id}>{p.name}</option>
                ))}
              </select>
            </div>
          </div>
          <button 
            type="button" 
            className="btn btn-primary" 
            style={{ width: '100%' }}
            onClick={() => {
              if (!batsmanId || !nonStrikerId || !bowlerId) {
                alert('All starting players must be selected!');
                return;
              }
              if (batsmanId === nonStrikerId) {
                alert('Striker and Non-Striker must be different players!');
                return;
              }
              setIsPlayersSetupCompleted(true);
            }}
          >
            Begin Scoring
          </button>
        </div>
      )}

      {/* STEP 4: RECORD BALL BY BALL */}
      {match.status === 'IN_PROGRESS' && activeInnings && isPlayersSetupCompleted && (
        <div className="card" style={styles.sectionCard}>
          <h2 style={{ marginBottom: '16px', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontSize: '1.4rem' }}>⚡</span> Live Scoring Logs
          </h2>

          {/* Active Batsmen & Bowler Individual stats banner */}
          <div style={styles.liveActivePlayersStats}>
            <div style={{ ...styles.activePlayerStatCard, borderLeft: '3px solid var(--accent-green)' }}>
              <span style={styles.activePlayerRole}>🏏 Striker</span>
              <strong style={styles.activePlayerName}>
                {currentBattingSquad.find(p => p.id === batsmanId)?.name || 'Striker'}
              </strong>
              <span style={styles.activePlayerFigures}>
                {strikerStats ? `${strikerStats.runs} (${strikerStats.ballsFaced}b) 4s:${strikerStats.fours} 6s:${strikerStats.sixes}*` : '0 (0b)*'}
              </span>
            </div>
            <div style={{ ...styles.activePlayerStatCard, borderLeft: '3px solid var(--text-muted)' }}>
              <span style={styles.activePlayerRole}>👤 Non-Striker</span>
              <strong style={styles.activePlayerName}>
                {currentBattingSquad.find(p => p.id === nonStrikerId)?.name || 'Non-Striker'}
              </strong>
              <span style={styles.activePlayerFigures}>
                {nonStrikerStats ? `${nonStrikerStats.runs} (${nonStrikerStats.ballsFaced}b) 4s:${nonStrikerStats.fours} 6s:${nonStrikerStats.sixes}` : '0 (0b)'}
              </span>
            </div>
            <div style={{ ...styles.activePlayerStatCard, borderLeft: '3px solid var(--accent-gold)' }}>
              <span style={styles.activePlayerRole}>🥎 Bowler</span>
              <strong style={styles.activePlayerName}>
                {currentBowlingSquad.find(p => p.id === bowlerId)?.name || 'Bowler'}
              </strong>
              <span style={styles.activePlayerFigures}>
                {currentBowlerStats ? `${currentBowlerStats.overs} ov, ${currentBowlerStats.wickets}-${currentBowlerStats.runsConceded} (Econ: ${currentBowlerStats.economy})` : '0.0 ov, 0-0'}
              </span>
            </div>
          </div>

          <form onSubmit={handleRecordBall}>
            {/* Active Players Selector Cards */}
            <div style={styles.squadSelectorsGrid}>
              <div style={styles.selectorCard}>
                <div style={styles.selectorHeader}>🏏 Striker</div>
                <select 
                  className="form-control" 
                  value={batsmanId} 
                  onChange={(e) => setBatsmanId(e.target.value)} 
                  required 
                  style={styles.selectCompact}
                >
                  <option value="">Select batsman...</option>
                  {currentBattingSquad.map(p => (
                    <option key={p.id} value={p.id}>{p.name}</option>
                  ))}
                </select>
              </div>

              <div style={styles.selectorCard}>
                <div style={styles.selectorHeader}>👤 Non-Striker</div>
                <select 
                  className="form-control" 
                  value={nonStrikerId} 
                  onChange={(e) => setNonStrikerId(e.target.value)} 
                  required 
                  style={styles.selectCompact}
                >
                  <option value="">Select batsman...</option>
                  {currentBattingSquad.map(p => (
                    <option key={p.id} value={p.id}>{p.name}</option>
                  ))}
                </select>
              </div>

              <div style={styles.selectorCard}>
                <div style={styles.selectorHeader}>🥎 Bowler</div>
                <select 
                  className="form-control" 
                  value={bowlerId} 
                  onChange={(e) => setBowlerId(e.target.value)} 
                  required 
                  style={styles.selectCompact}
                >
                  <option value="">Select bowler...</option>
                  {currentBowlingSquad.map(p => (
                    <option key={p.id} value={p.id}>{p.name}</option>
                  ))}
                </select>
              </div>
            </div>

            {/* Runs Off Bat Picker Grid */}
            <div style={{ marginBottom: '20px' }}>
              <label style={{ display: 'block', fontWeight: '700', marginBottom: '8px', fontSize: '0.9rem', color: 'var(--text-muted)' }}>
                RUNS OFF BAT
              </label>
              <div style={styles.runsGrid}>
                {[0, 1, 2, 3, 4, 6].map(val => (
                  <button
                    key={val}
                    type="button"
                    onClick={() => setRunsOffBat(val)}
                    style={{
                      ...styles.runButton,
                      background: runsOffBat === val ? 'var(--accent-green)' : 'rgba(255,255,255,0.03)',
                      borderColor: runsOffBat === val ? 'var(--accent-green)' : 'var(--border-glass)',
                      color: runsOffBat === val ? '#0f172a' : 'white',
                      fontWeight: runsOffBat === val ? '800' : '600',
                      boxShadow: runsOffBat === val ? '0 0 12px rgba(16, 185, 129, 0.4)' : 'none'
                    }}
                  >
                    {val === 4 ? '4 (4⃣)' : val === 6 ? '6 (6⃣)' : val}
                  </button>
                ))}
              </div>
            </div>

            {/* Extras Selection Section */}
            <div style={{ marginBottom: '20px' }}>
              <label style={{ display: 'block', fontWeight: '700', marginBottom: '8px', fontSize: '0.9rem', color: 'var(--text-muted)' }}>
                EXTRAS TYPE
              </label>
              <div style={styles.extrasFlex}>
                {[
                  { code: 'NONE', label: 'None (Legal)' },
                  { code: 'WD', label: 'Wide (+1)' },
                  { code: 'NB', label: 'No Ball (+1)' },
                  { code: 'LB', label: 'Leg Bye' },
                  { code: 'B', label: 'Bye' }
                ].map(item => (
                  <button
                    key={item.code}
                    type="button"
                    onClick={() => handleSelectExtraType(item.code)}
                    style={{
                      ...styles.extraChip,
                      background: extraType === item.code ? 'var(--accent-gold)' : 'rgba(255,255,255,0.03)',
                      borderColor: extraType === item.code ? 'var(--accent-gold)' : 'var(--border-glass)',
                      color: extraType === item.code ? '#0f172a' : 'white',
                      fontWeight: extraType === item.code ? 'bold' : 'normal',
                      boxShadow: extraType === item.code ? '0 0 10px rgba(245, 158, 11, 0.3)' : 'none'
                    }}
                  >
                    {item.label}
                  </button>
                ))}
              </div>

              {extraType !== 'NONE' && (
                <div style={{ marginTop: '12px', display: 'flex', alignItems: 'center', gap: '16px' }}>
                  <span style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>Extra Runs Conceded:</span>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <button
                      type="button"
                      onClick={() => setExtraRuns(Math.max(0, extraRuns - 1))}
                      style={styles.counterBtn}
                    >
                      -
                    </button>
                    <span style={{ fontSize: '1.2rem', fontWeight: 'bold', minWidth: '24px', textAlign: 'center' }}>
                      {extraRuns}
                    </span>
                    <button
                      type="button"
                      onClick={() => setExtraRuns(extraRuns + 1)}
                      style={styles.counterBtn}
                    >
                      +
                    </button>
                  </div>
                </div>
              )}
            </div>

            {/* Crimson Wicket Event Panel */}
            <div style={{
              ...styles.wicketBox,
              background: isWicket ? 'rgba(239, 68, 68, 0.08)' : 'rgba(239, 68, 68, 0.02)',
              borderColor: isWicket ? 'rgba(239, 68, 68, 0.3)' : 'rgba(239, 68, 68, 0.1)'
            }}>
              <label style={styles.checkboxLabel}>
                <input
                  type="checkbox"
                  checked={isWicket}
                  onChange={(e) => setIsWicket(e.target.checked)}
                  style={{ width: '18px', height: '18px' }}
                />
                <span style={{ fontWeight: '800', color: 'var(--accent-red)', fontSize: '0.95rem' }}>🚨 RECORD WICKET / OUT</span>
              </label>

              {isWicket && (
                <div style={{ marginTop: '16px' }}>
                  <label style={{ display: 'block', fontWeight: '700', marginBottom: '8px', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                    DISMISSAL TYPE
                  </label>
                  <div style={styles.extrasFlex}>
                    {['BOWLED', 'CAUGHT', 'LBW', 'STUMPED', 'RUN_OUT', 'HIT_WICKET', 'RETIRED'].map(type => (
                      <button
                        key={type}
                        type="button"
                        onClick={() => setDismissalType(type)}
                        style={{
                          ...styles.extraChip,
                          background: dismissalType === type ? 'var(--accent-red)' : 'rgba(255,255,255,0.03)',
                          borderColor: dismissalType === type ? 'var(--accent-red)' : 'var(--border-glass)',
                          color: dismissalType === type ? 'white' : 'var(--text-muted)',
                          fontSize: '0.8rem',
                          fontWeight: dismissalType === type ? 'bold' : 'normal'
                        }}
                      >
                        {type.replace('_', ' ')}
                      </button>
                    ))}
                  </div>

                  {dismissalType !== 'NONE' && dismissalType !== 'BOWLED' && dismissalType !== 'LBW' && dismissalType !== 'RETIRED' && (
                    <div style={{ marginTop: '16px' }}>
                      <label style={{ display: 'block', fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '6px' }}>
                        Fielder (Helper)
                      </label>
                      <select 
                        className="form-control" 
                        value={fielderId} 
                        onChange={(e) => setFielderId(e.target.value)} 
                        style={styles.selectCompact}
                      >
                        <option value="">Select fielder...</option>
                        {currentBowlingSquad.map(p => (
                          <option key={p.id} value={p.id}>{p.name}</option>
                        ))}
                      </select>
                    </div>
                  )}
                </div>
              )}
            </div>

            {/* Core scoring control triggers */}
            <div style={{ display: 'flex', gap: '16px', marginTop: '24px' }}>
              <button 
                type="submit" 
                className="btn btn-primary" 
                style={{ flex: 3, padding: '14px', fontSize: '1rem', fontWeight: '800', boxShadow: '0 4px 15px rgba(16, 185, 129, 0.2)' }}
              >
                Record Ball & Update Score
              </button>
              <button 
                type="button" 
                className="btn btn-secondary" 
                onClick={handleCompleteInnings} 
                style={{ flex: 1.5, padding: '14px', fontSize: '0.95rem' }}
              >
                End Innings
              </button>
            </div>
          </form>
        </div>
      )}

      {/* STEP 5: COMPLETE MATCH */}
      {match.status === 'INNINGS_BREAK' && !activeInnings && (
        <div className="card" style={styles.sectionCard}>
          <h2>End Innings / Complete Match</h2>
          <div style={{ display: 'flex', gap: '16px', marginBottom: '24px', flexWrap: 'wrap' }}>
            <button className="btn btn-primary" onClick={() => {
              setInningsNo(2);
              setBattingTeam(match.team2Id);
              setBowlingTeam(match.team1Id);
              alert('Configure start innings fields below to launch Innings 2.');
            }}>
              Prepare Innings 2 (Team 2 Bats, Team 1 Bowls)
            </button>
            <button className="btn btn-secondary" onClick={() => {
              setInningsNo(2);
              setBattingTeam(match.team1Id);
              setBowlingTeam(match.team2Id);
              alert('Configure start innings fields below to launch Innings 2.');
            }}>
              Prepare Innings 2 (Team 1 Bats, Team 2 Bowls)
            </button>
          </div>

          <form onSubmit={handleCompleteMatch} style={styles.completeMatchForm}>
            <h3>Conclude Match & Declare Winner</h3>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px', marginTop: '16px', marginBottom: '16px' }}>
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
      {/* WICKET OVERLAY (FULL SCREEN MODAL) */}
      {showWicketOverlay && (
        <div style={styles.modalOverlay}>
          <div style={{ ...styles.modalContent, maxWidth: '500px', textAlign: 'center' }}>
            <h2 style={{ color: 'var(--accent-red)', marginBottom: '8px' }}>🔴 Wicket!</h2>
            <p style={{ color: 'var(--text-muted)', marginBottom: '20px' }}>Select the next batsman to take strike.</p>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
              {getAvailableNextBatsmen().map(p => (
                <button
                  key={p.id}
                  type="button"
                  className="btn btn-secondary"
                  style={{ width: '100%', padding: '12px', fontSize: '1rem' }}
                  onClick={() => setBatsmanId(p.id)}
                >
                  {p.name} ({p.battingStyle?.replace('_', ' ')})
                </button>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* BOWLER OVERLAY (FULL SCREEN MODAL) */}
      {showBowlerOverlay && (
        <div style={styles.modalOverlay}>
          <div style={{ ...styles.modalContent, maxWidth: '500px', textAlign: 'center' }}>
            <h2 style={{ color: 'var(--accent-gold)', marginBottom: '8px' }}>🥎 Over Completed!</h2>
            <p style={{ color: 'var(--text-muted)', marginBottom: '20px' }}>Select the bowler for the next over.</p>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
              {getAvailableNextBowlers().map(p => (
                <button
                  key={p.id}
                  type="button"
                  className="btn btn-secondary"
                  style={{ width: '100%', padding: '12px', fontSize: '1rem' }}
                  onClick={() => setBowlerId(p.id)}
                >
                  {p.name} ({p.bowlingStyle !== 'NONE' ? p.bowlingStyle?.replace('_', ' ') : 'Bowler'})
                </button>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

const styles = {
  scoreboardBanner: {
    background: 'linear-gradient(135deg, rgba(15, 23, 42, 0.95), rgba(30, 41, 59, 0.95))',
    border: '1px solid rgba(16, 185, 129, 0.2)',
    boxShadow: 'var(--shadow-glow)',
    padding: '20px',
    marginBottom: '20px',
  },
  digitalScore: {
    fontSize: '2.2rem',
    fontWeight: '800',
    color: 'white',
    fontFamily: 'monospace, sans-serif',
    letterSpacing: '-1px'
  },
  sectionCard: {
    background: 'rgba(30, 41, 59, 0.35)',
    border: '1px solid var(--border-glass)',
    padding: '20px',
    marginBottom: '24px',
  },
  select: {
    background: '#1f2937',
    color: 'white',
    cursor: 'pointer',
  },
  selectCompact: {
    background: '#1f2937',
    color: 'white',
    cursor: 'pointer',
    fontSize: '0.85rem',
    padding: '8px',
    border: '1px solid var(--border-glass)'
  },
  squadSelectorsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
    gap: '12px',
    marginBottom: '20px'
  },
  selectorCard: {
    background: 'rgba(255,255,255,0.02)',
    border: '1px solid var(--border-glass)',
    borderRadius: '10px',
    padding: '12px'
  },
  selectorHeader: {
    fontSize: '0.8rem',
    fontWeight: 'bold',
    color: 'var(--text-muted)',
    marginBottom: '8px',
    textTransform: 'uppercase'
  },
  runsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(6, 1fr)',
    gap: '10px',
  },
  runButton: {
    padding: '16px 0',
    border: '1px solid',
    borderRadius: '12px',
    fontSize: '1.2rem',
    cursor: 'pointer',
    transition: 'all 0.15s ease'
  },
  extrasFlex: {
    display: 'flex',
    flexWrap: 'wrap',
    gap: '8px',
  },
  extraChip: {
    padding: '8px 16px',
    border: '1px solid',
    borderRadius: '20px',
    fontSize: '0.85rem',
    cursor: 'pointer',
    transition: 'all 0.15s ease'
  },
  counterBtn: {
    width: '32px',
    height: '32px',
    borderRadius: '50%',
    border: '1px solid var(--border-glass)',
    background: 'rgba(255,255,255,0.05)',
    color: 'white',
    fontSize: '1.2rem',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center'
  },
  wicketBox: {
    padding: '16px',
    border: '1px solid',
    borderRadius: '12px',
    marginTop: '20px',
    marginBottom: '20px',
    transition: 'all 0.2s ease'
  },
  squadSelectBox: {
    maxHeight: '260px',
    overflowY: 'auto',
    border: '1px solid var(--border-glass)',
    padding: '10px',
    borderRadius: '10px',
    background: 'rgba(255, 255, 255, 0.01)',
    display: 'flex',
    flexDirection: 'column',
    gap: '8px',
  },
  checkboxLabel: {
    display: 'flex',
    alignItems: 'center',
    gap: '10px',
    cursor: 'pointer',
  },
  completeMatchForm: {
    padding: '20px',
    border: '1px solid rgba(245, 158, 11, 0.2)',
    background: 'rgba(245, 158, 11, 0.01)',
    borderRadius: '10px',
  },
  liveActivePlayersStats: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
    gap: '12px',
    marginBottom: '20px'
  },
  activePlayerStatCard: {
    background: 'rgba(255, 255, 255, 0.02)',
    border: '1px solid var(--border-glass)',
    borderRadius: '10px',
    padding: '12px',
    display: 'flex',
    flexDirection: 'column',
    gap: '4px'
  },
  activePlayerRole: {
    fontSize: '0.75rem',
    color: 'var(--text-muted)',
    textTransform: 'uppercase',
    fontWeight: 'bold'
  },
  activePlayerName: {
    fontSize: '0.95rem',
    color: '#ffffff'
  },
  activePlayerFigures: {
    fontSize: '0.85rem',
    color: 'var(--accent-green)',
    fontWeight: 'bold',
    fontFamily: 'monospace'
  }
};

export default ScorerConsole;
