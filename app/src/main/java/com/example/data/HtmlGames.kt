package com.example.data

object HtmlGames {
    fun getSpaceRunnerHtml(appId: String, apiKey: String): String {
        return """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; user-select: none; }
  body { background: #0b0f19; color: #fff; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; overflow: hidden; height: 100vh; display: flex; flex-direction: column; }
  #header { display: flex; justify-content: space-between; align-items: center; padding: 10px 16px; background: #1a2236; border-bottom: 2px solid #ffb300; }
  .score { font-size: 18px; font-weight: bold; color: #ffca28; }
  .api-info { font-size: 11px; color: #4fc3f7; }
  #canvas-container { flex: 1; position: relative; width: 100%; height: 100%; }
  canvas { width: 100%; height: 100%; display: block; }
  #controls { position: absolute; bottom: 20px; left: 0; right: 0; display: flex; justify-content: space-between; padding: 0 30px; pointer-events: none; }
  .btn { width: 70px; height: 70px; border-radius: 50%; background: rgba(255,179,0,0.8); border: 3px solid #fff; color: #fff; font-size: 28px; display: flex; align-items: center; justify-content: center; pointer-events: auto; box-shadow: 0 4px 10px rgba(0,0,0,0.5); }
  .btn:active { background: #ff8f00; transform: scale(0.95); }
  #game-over { display: none; position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); background: rgba(16,24,40,0.95); border: 3px solid #ff5252; border-radius: 20px; padding: 24px; text-align: center; }
  #game-over h2 { color: #ff5252; font-size: 24px; margin-bottom: 10px; }
  #restart-btn { margin-top: 15px; padding: 12px 24px; font-size: 16px; font-weight: bold; border-radius: 12px; border: none; background: #4caf50; color: #fff; }
</style>
</head>
<body>
<div id="header">
  <div class="score">⭐ Koin Bintang: <span id="score-val">0</span></div>
  <div class="api-info">API: ${appId.take(6)}... | 🟢 Aktif</div>
</div>
<div id="canvas-container">
  <canvas id="gameCanvas"></canvas>
  <div id="controls">
    <div class="btn" id="btn-left">◀</div>
    <div class="btn" id="btn-right">▶</div>
  </div>
  <div id="game-over">
    <h2>💥 Ups! Tabrakan!</h2>
    <p>Skor Koin Kamu: <span id="final-score">0</span></p>
    <button id="restart-btn" onclick="restartGame()">Main Lagi 🚀</button>
  </div>
</div>

<script>
const canvas = document.getElementById('gameCanvas');
const ctx = canvas.getContext('2d');
let width, height;

function resize() {
  width = canvas.width = canvas.parentElement.clientWidth;
  height = canvas.height = canvas.parentElement.clientHeight;
}
window.addEventListener('resize', resize);
resize();

let player = { x: 0, y: 0, size: 40, speed: 7 };
let stars = [];
let meteors = [];
let score = 0;
let isOver = false;
let moveDir = 0;

function init() {
  player.x = width / 2;
  player.y = height - 90;
  stars = [];
  meteors = [];
  score = 0;
  isOver = false;
  document.getElementById('score-val').innerText = score;
  document.getElementById('game-over').style.display = 'none';
}

function spawnItems() {
  if (Math.random() < 0.05) {
    stars.push({ x: Math.random() * (width - 40) + 20, y: -20, size: 24, speed: 3.5 });
  }
  if (Math.random() < 0.035) {
    meteors.push({ x: Math.random() * (width - 40) + 20, y: -30, size: 35, speed: 4.5 });
  }
}

function update() {
  if (isOver) return;

  player.x += moveDir * player.speed;
  if (player.x < player.size / 2) player.x = player.size / 2;
  if (player.x > width - player.size / 2) player.x = width - player.size / 2;

  spawnItems();

  // Update stars
  for (let i = stars.length - 1; i >= 0; i--) {
    let s = stars[i];
    s.y += s.speed;
    let dist = Math.hypot(s.x - player.x, s.y - player.y);
    if (dist < player.size / 2 + s.size / 2) {
      stars.splice(i, 1);
      score += 10;
      document.getElementById('score-val').innerText = score;
    } else if (s.y > height + 20) {
      stars.splice(i, 1);
    }
  }

  // Update meteors
  for (let i = meteors.length - 1; i >= 0; i--) {
    let m = meteors[i];
    m.y += m.speed;
    let dist = Math.hypot(m.x - player.x, m.y - player.y);
    if (dist < player.size / 2 + m.size / 2) {
      isOver = true;
      document.getElementById('final-score').innerText = score;
      document.getElementById('game-over').style.display = 'block';
    } else if (m.y > height + 40) {
      meteors.splice(i, 1);
    }
  }
}

function draw() {
  ctx.fillStyle = '#0b0f19';
  ctx.fillRect(0, 0, width, height);

  // Background dust
  ctx.fillStyle = 'rgba(255,255,255,0.4)';
  for (let i = 0; i < 20; i++) {
    let bx = (Math.sin(i * 99 + Date.now() * 0.001) * 0.5 + 0.5) * width;
    let by = ((i * 77 + Date.now() * 0.05) % height);
    ctx.fillRect(bx, by, 2, 2);
  }

  // Draw Stars (Coins)
  for (let s of stars) {
    ctx.font = s.size + 'px sans-serif';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText('🪙', s.x, s.y);
  }

  // Draw Meteors
  for (let m of meteors) {
    ctx.font = m.size + 'px sans-serif';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText('☄️', m.x, m.y);
  }

  // Draw Player Rocket
  ctx.font = player.size + 'px sans-serif';
  ctx.textAlign = 'center';
  ctx.textBaseline = 'middle';
  ctx.fillText('🚀', player.x, player.y);
}

function loop() {
  update();
  draw();
  requestAnimationFrame(loop);
}

// Touch controls
const btnLeft = document.getElementById('btn-left');
const btnRight = document.getElementById('btn-right');

btnLeft.addEventListener('touchstart', (e) => { e.preventDefault(); moveDir = -1; });
btnLeft.addEventListener('touchend', (e) => { e.preventDefault(); if (moveDir === -1) moveDir = 0; });
btnRight.addEventListener('touchstart', (e) => { e.preventDefault(); moveDir = 1; });
btnRight.addEventListener('touchend', (e) => { e.preventDefault(); if (moveDir === 1) moveDir = 0; });

btnLeft.addEventListener('mousedown', () => { moveDir = -1; });
btnLeft.addEventListener('mouseup', () => { if (moveDir === -1) moveDir = 0; });
btnRight.addEventListener('mousedown', () => { moveDir = 1; });
btnRight.addEventListener('mouseup', () => { if (moveDir === 1) moveDir = 0; });

window.addEventListener('keydown', (e) => {
  if (e.key === 'ArrowLeft') moveDir = -1;
  if (e.key === 'ArrowRight') moveDir = 1;
});
window.addEventListener('keyup', (e) => {
  if (e.key === 'ArrowLeft' && moveDir === -1) moveDir = 0;
  if (e.key === 'ArrowRight' && moveDir === 1) moveDir = 0;
});

function restartGame() {
  init();
}

init();
loop();
</script>
</body>
</html>
        """.trimIndent()
    }

    fun getSnakeHtml(appId: String, apiKey: String): String {
        return """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<style>
  * { box-sizing: border-box; margin: 0; padding: 0; user-select: none; }
  body { background: #1b2838; color: #fff; font-family: sans-serif; text-align: center; height: 100vh; display: flex; flex-direction: column; justify-content: space-between; padding: 12px; }
  #header { display: flex; justify-content: space-between; align-items: center; background: #2a475e; padding: 10px 16px; border-radius: 12px; }
  .score { font-size: 18px; font-weight: bold; color: #4cbb17; }
  #canvas-box { flex: 1; display: flex; align-items: center; justify-content: center; }
  canvas { background: #0e1724; border: 3px solid #4caf50; border-radius: 16px; max-width: 95vw; max-height: 50vh; }
  #dpad { display: grid; grid-template-columns: repeat(3, 60px); grid-gap: 8px; justify-content: center; margin: 10px auto; }
  .dbtn { width: 60px; height: 50px; background: #ffb300; border-radius: 12px; font-size: 22px; border: none; font-weight: bold; color: #3e2723; }
  .dbtn:active { background: #ff8f00; }
</style>
</head>
<body>
<div id="header">
  <div class="score">🍎 Apel: <span id="score">0</span></div>
  <div style="font-size:12px; color:#ffca28;">MiniGame API Aktif</div>
</div>
<div id="canvas-box">
  <canvas id="snakeCanvas" width="300" height="300"></canvas>
</div>
<div id="dpad">
  <div></div>
  <button class="dbtn" onclick="setDir(0, -1)">▲</button>
  <div></div>
  <button class="dbtn" onclick="setDir(-1, 0)">◀</button>
  <button class="dbtn" onclick="setDir(0, 1)">▼</button>
  <button class="dbtn" onclick="setDir(1, 0)">▶</button>
</div>

<script>
const cvs = document.getElementById('snakeCanvas');
const ctx = cvs.getContext('2d');
const grid = 15;
let count = 0;
let score = 0;
let snake = { x: 150, y: 150, dx: grid, dy: 0, cells: [], maxCells: 4 };
let apple = { x: 60, y: 60 };

function getRandomInt(min, max) { return Math.floor(Math.random() * (max - min)) + min; }

function resetGame() {
  snake.x = 150; snake.y = 150; snake.cells = []; snake.maxCells = 4;
  snake.dx = grid; snake.dy = 0; score = 0;
  document.getElementById('score').innerText = score;
  apple.x = getRandomInt(0, 20) * grid;
  apple.y = getRandomInt(0, 20) * grid;
}

function setDir(x, y) {
  if (x !== 0 && snake.dx === 0) { snake.dx = x * grid; snake.dy = 0; }
  if (y !== 0 && snake.dy === 0) { snake.dy = y * grid; snake.dx = 0; }
}

function loop() {
  requestAnimationFrame(loop);
  if (++count < 6) return;
  count = 0;
  ctx.clearRect(0, 0, cvs.width, cvs.height);

  snake.x += snake.dx;
  snake.y += snake.dy;
  if (snake.x < 0) snake.x = cvs.width - grid;
  else if (snake.x >= cvs.width) snake.x = 0;
  if (snake.y < 0) snake.y = cvs.height - grid;
  else if (snake.y >= cvs.height) snake.y = 0;

  snake.cells.unshift({x: snake.x, y: snake.y});
  if (snake.cells.length > snake.maxCells) snake.cells.pop();

  // Draw apple
  ctx.fillStyle = '#ff5252';
  ctx.beginPath();
  ctx.arc(apple.x + grid/2, apple.y + grid/2, grid/2 - 1, 0, Math.PI*2);
  ctx.fill();

  // Draw snake
  ctx.fillStyle = '#4caf50';
  snake.cells.forEach(function(cell, index) {
    ctx.fillStyle = (index === 0) ? '#81c784' : '#4caf50';
    ctx.fillRect(cell.x, cell.y, grid-1, grid-1);
    if (cell.x === apple.x && cell.y === apple.y) {
      snake.maxCells++;
      score += 1;
      document.getElementById('score').innerText = score;
      apple.x = getRandomInt(0, 20) * grid;
      apple.y = getRandomInt(0, 20) * grid;
    }
    for (let i = index + 1; i < snake.cells.length; i++) {
      if (cell.x === snake.cells[i].x && cell.y === snake.cells[i].y) {
        resetGame();
      }
    }
  });
}
resetGame();
requestAnimationFrame(loop);
</script>
</body>
</html>
        """.trimIndent()
    }
}
