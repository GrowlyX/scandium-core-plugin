package com.solexgames.core.util;

import com.solexgames.core.player.grant.Grant;
import com.solexgames.core.player.ranks.Rank;
import lombok.experimental.UtilityClass;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author GrowlyX
 * @since 6/14/2021
 */

@UtilityClass
public class GrantUtil {

    public static final Grant DEF_GRANT = new Grant(null,Rank.getDefault(), System.currentTimeMillis(), -1L, "Automatic Grant (Default)", true, true);

    public static Grant getProminentGrant(List<Grant> grantList) {
        return grantList.stream()
                .sorted(Comparator.comparingLong(Grant::getDateAdded).reversed())
                .collect(Collectors.toList()).stream()
                .filter(grant -> grant != null && grant.getRank() != null && !grant.isRemoved() && grant.isActive() && !grant.getRank().isHidden() && (grant.getScope() == null || grant.isGlobal() || grant.isApplicable()))
                .findFirst().orElse(GrantUtil.DEF_GRANT);
    }
}
