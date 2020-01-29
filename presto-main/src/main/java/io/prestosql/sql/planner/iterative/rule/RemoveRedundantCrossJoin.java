/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.prestosql.sql.planner.iterative.rule;

import io.prestosql.matching.Captures;
import io.prestosql.matching.Pattern;
import io.prestosql.sql.planner.iterative.Rule;
import io.prestosql.sql.planner.plan.JoinNode;

import static io.prestosql.sql.planner.optimizations.QueryCardinalityUtil.isScalar;
import static io.prestosql.sql.planner.plan.Patterns.join;

public class RemoveRedundantCrossJoin
        implements Rule<JoinNode>
{
    private static final Pattern<JoinNode> PATTERN = join().matching(JoinNode::isCrossJoin);

    @Override
    public Pattern<JoinNode> getPattern()
    {
        return PATTERN;
    }

    @Override
    public Result apply(JoinNode node, Captures captures, Context context)
    {
        if (node.getRight().getOutputSymbols().isEmpty() && isScalar(node.getRight(), context.getLookup())) {
            return Result.ofPlanNode(node.getLeft());
        }
        if (node.getLeft().getOutputSymbols().isEmpty() && isScalar(node.getLeft(), context.getLookup())) {
            return Result.ofPlanNode(node.getRight());
        }
        return Result.empty();
    }
}