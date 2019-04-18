#!/usr/bin/env bash

set -e

DOMAIN=$(hostname -d)
FQDN=$(hostname -f)
REALM=$(echo "$DOMAIN"|awk '{print toupper($0)}')

ipa-server-install \
          --realm $REALM \
          --domain $DOMAIN \
          --hostname $FQDN \
          -a $FPW \
          -p $FPW \
          --setup-dns \
          --forwarder=8.8.8.8 \
          --auto-reverse \
          --ssh-trust-dns \
          --mkhomedir \
          --unattended

set +e