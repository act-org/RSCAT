#!/bin/sh

mkdir -p /var/log/shiny-server
chown shiny.shiny /var/log/shiny-server

if [ "$APPLICATION_LOGS_TO_STDOUT" != "false" ];
then
    exec xtail /var/log/shiny-server/ &
fi

exec shiny-server 2>&1
