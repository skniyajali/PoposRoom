#!/bin/bash

#
#      Copyright 2024 Sk Niyaj Ali
#
#      Licensed under the Apache License, Version 2.0 (the "License");
#      you may not use this file except in compliance with the License.
#      You may obtain a copy of the License at
#
#              http://www.apache.org/licenses/LICENSE-2.0
#
#      Unless required by applicable law or agreed to in writing, software
#      distributed under the License is distributed on an "AS IS" BASIS,
#      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#      See the License for the specific language governing permissions and
#      limitations under the License.
#

# Check if gradlew exists in the project
if [ ! -f "./gradlew" ]; then
    echo "Error: gradlew not found in the project."
    exit 1
fi

echo "Starting all checks and tests..."

failed_tasks=()
successful_tasks=()

run_gradle_task() {
    echo "Running: $1"
    "./gradlew" $1
    if [ $? -ne 0 ]; then
        echo "Warning: Task $1 failed"
        failed_tasks+=("$1")
    else
        echo "Task $1 completed successfully"
        successful_tasks+=("$1")
    fi
}

tasks=(
    "check -p build-logic"
    "spotlessApply --no-configuration-cache"
    "dependencyGuardBaseline"
    "detekt"
    "testDemoDebug -P roborazzi.test.verify=false :lint:test"
    ":app:lintProdRelease :lint:test :lint:lint"
    "assembleDemoDebug updateProdReleaseBadging"
)

for task in "${tasks[@]}"; do
    run_gradle_task "$task"
done

echo "All tasks have finished."

echo "Successful tasks:"
for task in "${successful_tasks[@]}"; do
    echo "- $task"
done

if [ ${#failed_tasks[@]} -eq 0 ]; then
    echo "All checks and tests completed successfully."
else
    echo "Failed tasks:"
    for task in "${failed_tasks[@]}"; do
        echo "- $task"
    done
    echo "Please review the output above for more details on the failures."
fi

echo "Total tasks: ${#tasks[@]}"
echo "Successful tasks: ${#successful_tasks[@]}"
echo "Failed tasks: ${#failed_tasks[@]}"