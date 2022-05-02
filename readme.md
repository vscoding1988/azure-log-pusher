# Azure Log pusher

The application will send local logfiles to Log Analytics Workspace for better analysing. It will use the microsoft API (doc can be found [here](https://docs.microsoft.com/en-us/azure/azure-monitor/logs/data-collector-api)).

After starting the backend (see [here](backend/readme.md)) you can use the rest endpoint of the application to send your
local logs to the Log Analytics Workspace. 

## Features
- process and send a single local log-file to azure 
  - each line is processed as one log entry (in other words, stacktrace is not supported)

## TODO
- Allow tailing
- support folders
- add basic frontend
- make application standalone
- add support for multi line logs (like exceptions) 
