#!/bin/bash
# Flutter Development Server with Hot Reload
# This script starts the Flutter web development server with hot reload enabled

echo "🚀 Starting Flutter Development Server..."
echo ""
echo "📝 Features:"
echo "   - Hot Reload enabled (press 'r' to reload)"
echo "   - CORS configured for localhost:8080 (API Gateway)"
echo "   - Server accessible from Windows browser"
echo ""
echo "🌐 Access your app at:"
echo "   - http://localhost:50000"
echo "   - http://172.27.36.171:50000 (from Windows)"
echo ""
echo "⌨️  Hot Reload Commands:"
echo "   r - Hot reload 🔥🔥🔥"
echo "   R - Hot restart (full reload)"
echo "   q - Quit"
echo ""

flutter run -d web-server --web-port=50000 --web-hostname=0.0.0.0
