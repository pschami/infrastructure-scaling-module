#!/bin/bash
#
# Copyright (c) 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
# Dell EMC Confidential/Proprietary Information
#

echo "Removing Dell Inc. APM Nagios components"

systemctl disable scaling-module

echo "Dell Inc. APM Nagios components removal has completed successfully."

exit 0
