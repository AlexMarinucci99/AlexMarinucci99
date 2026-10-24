#!/usr/bin/env bash
# Genera un banner GIF e la relativa anteprima statica per il README GitHub.
# Collocare questo script nella root del repository AlexMarinucci99.
set -euo pipefail
cd "$(dirname "$0")"

if ! command -v ffmpeg >/dev/null 2>&1; then
  echo 'Errore: FFmpeg non installato. Esegui: sudo apt update && sudo apt install ffmpeg -y' >&2
  exit 1
fi

FRAMES='assets/banner/frames'
OUT='assets/banner/output'
mkdir -p "$OUT"
for i in 01 02 03 04; do
  if [[ ! -f "$FRAMES/frame_${i}.png" ]]; then
    echo "Errore: manca $FRAMES/frame_${i}.png" >&2
    exit 1
  fi
done

BOLD_FONT=$(fc-match -f '%{file}' 'DejaVu Sans:style=Bold')
REG_FONT=$(fc-match -f '%{file}' 'DejaVu Sans')

# Riporta ogni frame dentro 1200x320 senza tagliare il soggetto.
PREP='scale=1200:320:force_original_aspect_ratio=decrease:flags=lanczos,pad=1200:320:(ow-iw)/2:(oh-ih)/2:color=0x070B13,fps=12,setsar=1,format=yuv420p'
TEXT="drawtext=fontfile=${BOLD_FONT}:text='Alessandro Marinucci':fontcolor=white:fontsize=44:x=55:y=103:shadowcolor=black@0.9:shadowx=2:shadowy=2,drawtext=fontfile=${REG_FONT}:text='Full-Stack Developer':fontcolor=0x69D2F4:fontsize=27:x=57:y=164:shadowcolor=black@0.9:shadowx=2:shadowy=2"

FILTER="[0:v]${PREP}[v0];[1:v]${PREP}[v1];[2:v]${PREP}[v2];[3:v]${PREP}[v3];[4:v]${PREP}[v4];"
FILTER+="[v0][v1]xfade=transition=fade:duration=0.75:offset=1.25[x1];"
FILTER+="[x1][v2]xfade=transition=fade:duration=0.75:offset=2.50[x2];"
FILTER+="[x2][v3]xfade=transition=fade:duration=0.75:offset=3.75[x3];"
FILTER+="[x3][v4]xfade=transition=fade:duration=0.75:offset=5.00[x4];"
FILTER+="[x4]${TEXT},split[a][b];[a]palettegen=max_colors=128:stats_mode=diff[p];[b][p]paletteuse=dither=bayer:bayer_scale=3[out]"

printf '\nCreo la GIF animata (7 secondi)...\n'
ffmpeg -hide_banner -loglevel warning -y \
  -loop 1 -t 2 -i "$FRAMES/frame_01.png" \
  -loop 1 -t 2 -i "$FRAMES/frame_02.png" \
  -loop 1 -t 2 -i "$FRAMES/frame_03.png" \
  -loop 1 -t 2 -i "$FRAMES/frame_04.png" \
  -loop 1 -t 2 -i "$FRAMES/frame_01.png" \
  -filter_complex "$FILTER" -map '[out]' -loop 0 "$OUT/alex-banner-final.gif"

printf '\nCreo la PNG statica...\n'
ffmpeg -hide_banner -loglevel warning -y -i "$FRAMES/frame_04.png" \
  -vf "scale=1200:320:force_original_aspect_ratio=decrease:flags=lanczos,pad=1200:320:(ow-iw)/2:(oh-ih)/2:color=0x070B13,${TEXT}" \
  -frames:v 1 -update 1 "$OUT/alex-banner-static.png"

printf '\nFile generati:\n'
ls -lh "$OUT/alex-banner-final.gif" "$OUT/alex-banner-static.png"
printf '\nApri la GIF con: xdg-open assets/banner/output/alex-banner-final.gif\n'
