from flask import Flask, request, jsonify
from flask_cors import CORS
import sqlite3
from datetime import datetime

app = Flask(__name__)
CORS(app)

# Initialize database
def init_db():
    with sqlite3.connect("runs.db") as conn:
        cursor = conn.cursor()
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS runs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                latitude REAL,
                longitude REAL,
                distance REAL,
                timestamp TEXT
            )
        ''')
        conn.commit()

init_db()

# Log a new run
@app.route("/api/log_run", methods=["POST"])
def log_run():
    data = request.json
    position = data.get("position")
    distance = data.get("distance")
    
    if position and distance is not None:
        timestamp = datetime.now().isoformat()
        with sqlite3.connect("runs.db") as conn:
            cursor = conn.cursor()
            cursor.execute("INSERT INTO runs (latitude, longitude, distance, timestamp) VALUES (?, ?, ?, ?)",
                           (position["lat"], position["lon"], distance, timestamp))
            conn.commit()
        
        return jsonify({"message": "Run logged!", "data": {"latitude": position["lat"], "longitude": position["lon"], "distance": distance, "timestamp": timestamp}}), 201
    
    return jsonify({"error": "Invalid data"}), 400

# Retrieve all runs
@app.route("/api/get_runs", methods=["GET"])
def get_runs():
    with sqlite3.connect("runs.db") as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT latitude, longitude, distance, timestamp FROM runs")
        runs = [{"latitude": row[0], "longitude": row[1], "distance": row[2], "timestamp": row[3]} for row in cursor.fetchall()]
    
    return jsonify(runs), 200

if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=5000)
