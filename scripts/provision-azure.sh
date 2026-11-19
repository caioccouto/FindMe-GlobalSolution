#!/usr/bin/env bash
set -euo pipefail

RG="rg-findme-devops"
VM="vm-findme"
LOCATION="${AZURE_LOCATION:-centralus}"
SIZE="${AZURE_VM_SIZE:-Standard_D2alds_v7}"
ADMIN="azureuser"

az account show -o table

az group create \
  --name "$RG" \
  --location "$LOCATION"

mkdir -p ~/.ssh

if [[ ! -f ~/.ssh/id_rsa.pub ]]; then
  ssh-keygen -q -t rsa -b 4096 \
    -f ~/.ssh/id_rsa -N ''
fi

az vm create \
  --resource-group "$RG" \
  --name "$VM" \
  --location "$LOCATION" \
  --image Ubuntu2404 \
  --size "$SIZE" \
  --admin-username "$ADMIN" \
  --authentication-type ssh \
  --ssh-key-values ~/.ssh/id_rsa.pub \
  --public-ip-sku Standard \
  --priority Spot \
  --eviction-policy Deallocate

az vm open-port \
  --resource-group "$RG" \
  --name "$VM" \
  --port 8080 \
  --priority 1010

az vm show \
  -g "$RG" \
  -n "$VM" \
  --show-details \
  -o table

az vm show \
  -g "$RG" \
  -n "$VM" \
  --show-details \
  --query publicIps \
  -o tsv
