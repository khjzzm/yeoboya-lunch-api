#!/bin/bash

SCRIPT=$1
ENV_FILE=${2:-env/default.env}
BASENAME=$(basename "$SCRIPT" .js)
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
OUT_JSON="results/${BASENAME}_${TIMESTAMP}.json"
OUT_HTML="results/${BASENAME}_${TIMESTAMP}.html"

if [ -z "$SCRIPT" ]; then
  echo "Usage: ./run.sh [script file] [env file (optional)]"
  exit 1
fi

mkdir -p results

docker run --rm \
  -v $(pwd)/scripts:/scripts \
  -v $(pwd)/results:/app/results \
  --env-file $(pwd)/$ENV_FILE \
  grafana/k6 run /scripts/$SCRIPT

# 결과 파일 리네이밍
if [ -f results/summary.json ]; then
  mv results/summary.json "$OUT_JSON"
fi

if [ -f results/summary.html ]; then
  mv results/summary.html "$OUT_HTML"
  echo "📄 HTML report available at $OUT_HTML"
fi

echo "✅ Test completed. JSON saved to $OUT_JSON"