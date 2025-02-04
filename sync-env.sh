#!/bin/bash
export VAULT_ADDR=$(cat vault-secret.json|jq -r '.VAULT_ADDR')
export VAULT_TOKEN=$(cat vault-secret.json|jq -r '.VAULT_TOKEN')
SECRETS=$(vault kv get -format json -mount="digimatka/noheva/local/kv" "build-muisti-exhibition-ui")

SIGN_KEYSTORE=$(echo $SECRETS|jq -r '.data.data.SIGN_KEYSTORE')
SIGN_STORE_PASSWORD=$(echo $SECRETS|jq -r '.data.data.SIGN_STORE_PASSWORD')
SIGN_KEY_ALIAS=$(echo $SECRETS|jq -r '.data.data.SIGN_KEY_ALIAS')
SIGN_KEY_PASSWORD=$(echo $SECRETS|jq -r '.data.data.SIGN_KEY_PASSWORD')

echo SIGN_STORE_PASSWORD=$SIGN_STORE_PASSWORD > app/local.properties
echo SIGN_KEY_ALIAS=$SIGN_KEY_ALIAS >> app/local.properties
echo SIGN_KEY_PASSWORD=$SIGN_KEY_PASSWORD >> app/local.properties

echo $SIGN_KEYSTORE > /tmp/upload-keystore.b64 && base64 -d -i /tmp/upload-keystore.b64 > /tmp/upload-keystore.jks
echo SIGN_STORE_FILE=/tmp/upload-keystore.jks >> app/local.properties

echo MUISTI_API_BASE_URL=$(echo $SECRETS|jq -r '.data.data.MUISTI_API_BASE_URL') > app/.env
echo KEYCLOAK_URL=$(echo $SECRETS|jq -r '.data.data.KEYCLOAK_URL') >> app/.env
echo MQTT_BASE_TOPIC=$(echo $SECRETS|jq -r '.data.data.MQTT_BASE_TOPIC') >> app/.env
echo KEYCLOAK_REALM=$(echo $SECRETS|jq -r '.data.data.KEYCLOAK_REALM') >> app/.env
echo KEYCLOAK_CLIENT_ID=$(echo $SECRETS|jq -r '.data.data.KEYCLOAK_CLIENT_ID') >> app/.env
echo KEYCLOAK_USERNAME=$(echo $SECRETS|jq -r '.data.data.KEYCLOAK_USERNAME') >> app/.env
echo KEYCLOAK_PASSWORD=$(echo $SECRETS|jq -r '.data.data.KEYCLOAK_PASSWORD') >> app/.env
echo KEYCLOAK_DEMO_TAG=$(echo $SECRETS|jq -r '.data.data.KEYCLOAK_DEMO_TAG') >> app/.env
echo DESTRUCTIVE_MIGRATIONS=$(echo $SECRETS|jq -r '.data.data.DESTRUCTIVE_MIGRATIONS') >> app/.env
echo MQTT_URLS=$(echo $SECRETS|jq -r '.data.data.MQTT_URLS') >> app/.env
echo MQTT_PASSWORD=$(echo $SECRETS|jq -r '.data.data.MQTT_PASSWORD') >> app/.env
echo MQTT_USER=$(echo $SECRETS|jq -r '.data.data.MQTT_USER') >> app/.env
echo MAP_BOX_ACCESS_TOKEN=$(echo $SECRETS|jq -r '.data.data.MAP_BOX_ACCESS_TOKEN') >> app/.env
echo UPDATE_URL=$(echo $SECRETS|jq -r '.data.data.UPDATE_URL') >> app/.env

