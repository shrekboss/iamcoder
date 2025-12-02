package org.coder.algorithm.programming;

import java.util.regex.Pattern;

public class IPv4Converter {
    
    public static Long ipv4ToInt(String ipStr) {
        // 检查是否包含非法字符（只允许数字、点、空格）
        if (!Pattern.matches("[0-9. ]+", ipStr)) {
            return null; // 非法地址
        }
        
        // 检查空格位置是否合法
        // 空格只能出现在数字和点之间，即：
        // 1. 空格不能出现在开头或结尾
        // 2. 空格前后必须是数字和点（或点和数字）
        // 3. 不能有连续的空格（虽然题目没说，但实际中应该避免）
        if (ipStr.startsWith(" ") || ipStr.endsWith(" ")) {
            return null;
        }
        
        // 预处理：去除所有合法空格
        // 注意：这里我们只在确认空格位置合法的情况下才去除
        String processed = ipStr;
        
        // 检查空格是否合法并去除
        for (int i = 0; i < processed.length(); i++) {
            if (processed.charAt(i) == ' ') {
                // 检查空格前后字符
                if (i == 0 || i == processed.length() - 1) {
                    return null; // 空格在开头或结尾
                }
                
                char prev = processed.charAt(i - 1);
                char next = processed.charAt(i + 1);
                
                // 空格前是数字，后是点；或者前是点，后是数字
                boolean isValidSpace = 
                    (Character.isDigit(prev) && next == '.') ||
                    (prev == '.' && Character.isDigit(next));
                
                if (!isValidSpace) {
                    return null; // 非法空格位置
                }
            }
        }
        
        // 去除所有空格
        processed = processed.replaceAll(" ", "");
        
        // 检查是否是合法的IPv4格式
        if (!isValidIPv4(processed)) {
            return null;
        }
        
        // 转换为32位整数
        return convertIPv4ToLong(processed);
    }
    
    private static boolean isValidIPv4(String ip) {
        String[] parts = ip.split("\\.");
        
        // 必须有4个部分
        if (parts.length != 4) {
            return false;
        }
        
        for (String part : parts) {
            // 检查每个部分是否为空
            if (part.isEmpty()) {
                return false;
            }
            
            // 检查是否只包含数字
            if (!part.matches("\\d+")) {
                return false;
            }
            
            // 检查是否有前导零（除了"0"本身）
            if (part.length() > 1 && part.startsWith("0")) {
                return false;
            }
            
            // 检查数值范围
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false; // 数字太大，超出int范围
            }
        }
        
        return true;
    }
    
    private static Long convertIPv4ToLong(String ip) {
        String[] parts = ip.split("\\.");
        long result = 0;
        
        for (int i = 0; i < 4; i++) {
            int num = Integer.parseInt(parts[i]);
            // 将每个部分左移相应的位数并相加
            result |= ((long) num << (24 - 8 * i));
        }
        
        // 为了确保是32位无符号整数，与0xFFFFFFFFL进行与运算
        return result & 0xFFFFFFFFL;
    }
    
    // 测试方法
    public static void main(String[] args) {
        // 测试用例
        String[] testCases = {
            "192.168.1.1",        // 正常地址
            "192 .168.1.1",       // 合法空格
            "192. 168.1.1",       // 合法空格
            "192.168 .1.1",       // 合法空格
            "192.168.1 .1",       // 合法空格
            "192 . 168 . 1 . 1",  // 合法空格
            " 192.168.1.1",       // 非法：开头空格
            "192.168.1.1 ",       // 非法：结尾空格
            "192.168.1",          // 非法：只有3部分
            "192.168.1.1.1",      // 非法：5部分
            "192.168.1.256",      // 非法：超出范围
            "192.168.01.1",       // 非法：前导零
            "192.a.1.1",          // 非法：包含字母
            "192.168.1  .1",      // 非法：连续空格（根据题目应为非法）
            "192 .168. 1.1"       // 合法：空格在数字和点之间
        };
        
        for (String testCase : testCases) {
            Long result = ipv4ToInt(testCase);
            System.out.printf("%-25s -> ", "\"" + testCase + "\"");
            if (result != null) {
                // 以无符号32位整数形式输出
                System.out.printf("0x%08X (%d)%n", result, result);
            } else {
                System.out.println("非法地址");
            }
        }
    }
}