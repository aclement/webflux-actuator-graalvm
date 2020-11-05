#!/usr/bin/env bash
RESPONSE=`curl -s localhost:8080/actuator/health`
echo "R1 $RESPONSE"
if [[ "$RESPONSE" == *"UP"* ]]; then
  RESPONSE=`curl -s localhost:8080/reservations`
  echo "R2 $RESPONSE"
  if [[ "$RESPONSE" == *"Josh"* ]]; then
    RESPONSE=`curl localhost:8080/actuator/health/readiness`
    echo "R3 $RESPONSE"
    if [[ "$RESPONSE" == *'{"status":"UP"}'* ]]; then
      RESPONSE=`curl localhost:8080/actuator/health/liveness`
      if [[ "$RESPONSE" == *'{"status":"UP"}'* ]]; then
        RESPONSE=`curl -X POST localhost:8080/actuator/shutdown`
        if [[ "$RESPONSE" == *'{"message":"Shutting down, bye..."}'* ]]; then
  	  exit 0
	fi
      fi
    fi
  fi
fi
echo $RESPONSE
exit 1
