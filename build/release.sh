# Licensed to the Smolok project under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#!/usr/bin/env bash

set -e

mvn -Darguments='-Dmaven.test.skip=true' release:prepare release:perform

OLD_VERSION=0.0.4
NEW_VERSION=0.0.5

sed -i -e "s/${OLD_VERSION}/${NEW_VERSION}/g" readme.md platform/cmd/src/main/bash/cmd

mvn clean install -Pdocker -DskipTests