#!/usr/bin/env bash
set -x
gst-launch-1.0 -v \
  audiotestsrc wave=sine freq=400 is-live=true ! \
  audio/x-raw,format=S16BE,channels=1,rate=48000,layout=interleaved ! \
  rtpL16pay ! \
  udpsink host=127.0.0.1 port=5000$1
