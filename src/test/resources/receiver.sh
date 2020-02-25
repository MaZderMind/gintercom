#!/usr/bin/env bash
set -x
gst-launch-1.0 -v \
  udpsrc port=4000$1 ! \
  application/x-rtp,clock-rate=48000,media=audio,encoding-name=L16 ! \
  rtpL16depay ! \
  audioconvert ! \
  audio/x-raw,format=S16LE,channels=1,rate=48000,layout=interleaved ! \
  alsasink
