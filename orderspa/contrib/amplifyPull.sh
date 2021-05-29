set -e
IFS='|'

AWS_PROFILE=${AWS_PROFILE:=default}
echo AWS_PROFILE $AWS_PROFILE
echo BUILD_ENV $BUILD_ENV

SVELTECONFIG="{\
\"SourceDir\":\"src\",\
\"DistributionDir\":\"dist\",\
\"BuildCommand\":\"npm run-script build\",\
\"StartCommand\":\"npm run-script dev\"\
}"

AWSCLOUDFORMATIONCONFIG="{\
\"configLevel\":\"project\",\
\"useProfile\":true,\
\"profileName\":\"$AWS_PROFILE\",\
\"region\":\"$AWS_REGION\"\
}"

AMPLIFY="{\
\"projectName\":\"orderspa\",\
\"appId\":\"$AWS_APP_ID\",\
\"envName\":\"$BUILD_ENV\",\
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

echo "n\n" | amplify pull \
--amplify $AMPLIFY \
--frontend $FRONTEND \
--providers $PROVIDERS
