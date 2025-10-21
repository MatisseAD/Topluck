# Topluck - Anti-Cheat Plugin for Minecraft

Topluck is a comprehensive anti-cheat system for Minecraft servers, designed to detect and prevent cheating, especially X-ray mining and abnormal ore mining patterns.

## Features

### 1. Automatic Alert System
- **Threshold-based Detection**: Automatically alerts admins when players exceed configurable thresholds for rare ore mining percentages
- **Multiple Ore Types**: Monitors diamonds, emeralds, ancient debris, and combined rare ore percentages
- **Real-time Notifications**: Online admins with the `topluck.alerts` permission receive instant notifications
- **Alert Logging**: All alerts are logged to `plugins/Topluck/alerts/alerts.log`

### 2. Alert History
- Complete history of all alerts with timestamps
- Track which alerts have been handled and by whom
- Filter alerts by player or view all pending alerts
- Alert statuses: PENDING, HANDLED, DISMISSED

### 3. Player Surveillance System
- Mark players as "UNDER_SURVEILLANCE" with custom reasons
- Mark players as "CLEARED" after investigation
- Track surveillance start dates
- Surveillance status visible in stats displays

### 4. Suspicion Score
- Automatic calculation of suspicion scores (0-100)
- Based on mining percentages compared to normal patterns
- Color-coded display (green/yellow/red)
- Visible in stats menu and player info

### 5. Detailed Audit Reports
- Comprehensive player mining statistics
- Comparison with server averages
- Alert history for the player
- Surveillance status and history
- Location tracking of rare ore mining

### 6. Location Tracking
- Records the location of every rare ore mined
- Includes world name and coordinates
- Useful for pattern detection (e.g., straight-line mining at diamond level)
- Mining history stored per player

### 7. Admin Tools
- **Freeze Command**: Temporarily freeze players during investigation
- **Surveillance Management**: Mark players for surveillance or clear them
- **Export Functionality**: Placeholder for CSV/JSON export of player stats
- **Alert Management**: View, handle, and dismiss alerts

### 8. Server Average Comparison
- Automatic calculation of server-wide mining averages
- Compare individual players to server averages
- Helps identify outliers and suspicious behavior

### 9. Stats Menu (GUI)
- Interactive inventory-based GUI
- Shows all online players with their stats
- Color-coded percentages based on suspicion
- Displays surveillance status
- Shows suspicion scores

## Commands

### Player Commands
- `/topluck` or `/tl` - Open stats menu (GUI)
- `/topluck <player>` - View detailed stats for a specific player

### Admin Commands
- `/tlad audit <player>` - Generate detailed audit report
- `/tlad surveillance <player> <watch|clear> [reason]` - Manage surveillance status
- `/tlad alerts [player]` - View alerts (all pending or for specific player)
- `/tlad freeze <player>` - Freeze/unfreeze a player
- `/tlad export <player> <csv|json>` - Export player stats

## Permissions

- `topluck.admin` - Access to all admin commands (default: op)
- `topluck.alerts` - Receive alert notifications (default: op)

## Configuration

The `config.yml` file allows you to customize:

### Thresholds
```yaml
thresholds:
  diamond: 5.0      # Alert if > 5% of blocks are diamonds
  emerald: 3.0      # Alert if > 3% of blocks are emeralds
  ancient_debris: 2.0  # Alert if > 2% of blocks are ancient debris
  rare_combined: 8.0   # Alert if > 8% of blocks are rare ores combined
```

### Alert Settings
```yaml
alerts:
  enabled: true           # Enable/disable alert system
  notify-admins: true     # Send in-game notifications
  log-to-file: true       # Log alerts to file
```

### Minimum Blocks
```yaml
minimum-blocks: 100  # Minimum blocks mined before alerts are generated
```

## How It Works

1. **Mining Detection**: The plugin monitors all block breaks and tracks ore mining
2. **Statistics Tracking**: Maintains per-player statistics including:
   - Total blocks mined
   - Ore counts by type
   - Mining locations for rare ores
   - Mining history with timestamps
3. **Alert Generation**: When rare ores are mined, checks if percentages exceed thresholds
4. **Admin Notification**: Alerts are logged and admins are notified in real-time
5. **Investigation Tools**: Admins can audit players, freeze them, and manage surveillance
6. **Suspicion Scoring**: Automatic calculation helps prioritize investigations

## Anti-Cheat Features Implemented

From the feature request list:

- ✅ Automatic alerts for abnormal mining percentages
- ✅ Alert history with timestamps and handling status
- ✅ Admin dashboard (stats menu GUI)
- ✅ Suspicion score/risk ranking system
- ✅ Admin notifications for suspicious activity
- ✅ Surveillance status management (watch/cleared)
- ✅ Server average comparison
- ✅ Location tracking for mining events
- ✅ Export functionality framework (CSV/JSON)
- ✅ Detailed audit reports
- ✅ Freeze command for investigations
- ✅ Alert filtering and management

## Normal Mining Percentages

For reference, legitimate mining typically shows:
- Diamonds: ~0.1-0.3% of blocks mined
- Emeralds: ~0.05-0.2% of blocks mined
- Ancient Debris: ~0.1-0.3% of blocks mined (in Nether)

Players significantly exceeding these percentages (especially with low total block counts) are likely using X-ray or similar cheats.

## Installation

1. Download the plugin JAR file
2. Place it in your server's `plugins` folder
3. Restart the server
4. Configure thresholds in `plugins/Topluck/config.yml`
5. Reload with `/reload` or restart the server

## Support

This plugin is designed for Paper/Spigot 1.21+ servers running Java 21.
