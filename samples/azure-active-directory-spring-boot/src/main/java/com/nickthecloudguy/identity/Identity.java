package com.nickthecloudguy.identity;

import java.util.List;

public record Identity(String name, List<String> claims, List<String> authorities) {
}
