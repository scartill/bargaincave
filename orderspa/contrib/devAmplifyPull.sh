#!/bin/bash
set -e
IFS='|'

SVELTECONFIG="{\
\"SourceDir\":\"src\",\
\"DistributionDir\":\"dist\",\
\"BuildCommand\":\"npm run-script build\",\
\"StartCommand\":\"npm run-script dev\"\
}"

AWSCLOUDFORMATIONCONFIG="{\
\"useProfile\":true,\
\"profileName\":$AWS_PROFILE,\
}"

AMPLIFY="{\
\"projectName\":\"orderspa\",\
\"appId\":\"$AWS_APP_ID\",\
\"envName\":\"staging\",\
\"defaultEditor\":\"vscode\"\
}"

FRONTEND="{\
\"frontend\":\"javascript\",\
\"framework\":\"none\",\
\"config\":$SVELTECONFIG\
}"

PROVIDERS="{\
\"awscloudformation\":$AWSCLOUDFORMATIONCONFIG\
}"

amplify pull \
--amplify $AMPLIFY \
--frontend $FRONTEND \
--providers $PROVIDERS 
