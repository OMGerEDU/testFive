package test.five.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Simple JSON parser and serializer supporting objects, arrays, strings,
 * numbers, booleans and null. It is intentionally minimal and only
 * implements features required by the profile persistence layer. */
class SimpleJson {
    static Object parse(String json) {
        return new Parser(json).parseValue();
    }

    static String stringify(Object value) {
        StringBuilder sb = new StringBuilder();
        writeValue(sb, value);
        return sb.toString();
    }

    private static void writeValue(StringBuilder sb, Object value) {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof String) {
            sb.append('"').append(escape((String) value)).append('"');
        } else if (value instanceof Number || value instanceof Boolean) {
            sb.append(value);
        } else if (value instanceof Map) {
            sb.append('{');
            boolean first = true;
            for (Map.Entry<?,?> e : ((Map<?,?>) value).entrySet()) {
                if (!first) sb.append(',');
                writeValue(sb, e.getKey().toString());
                sb.append(':');
                writeValue(sb, e.getValue());
                first = false;
            }
            sb.append('}');
        } else if (value instanceof List) {
            sb.append('[');
            boolean first = true;
            for (Object o : (List<?>) value) {
                if (!first) sb.append(',');
                writeValue(sb, o);
                first = false;
            }
            sb.append(']');
        } else {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass());
        }
    }

    private static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20 || c > 0x7E) {
                        String hex = Integer.toHexString(c);
                        sb.append("\\u");
                        for (int j = hex.length(); j < 4; j++) sb.append('0');
                        sb.append(hex);
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    private static class Parser {
        private final String s;
        private int pos = 0;
        Parser(String s) { this.s = s; }

        Object parseValue() {
            skipWhitespace();
            if (pos >= s.length()) throw new RuntimeException("Unexpected end of JSON");
            char c = s.charAt(pos);
            switch (c) {
                case '{': return parseObject();
                case '[': return parseArray();
                case '"': return parseString();
                case 't': return parseTrue();
                case 'f': return parseFalse();
                case 'n': return parseNull();
                default: return parseNumber();
            }
        }

        private Map<String,Object> parseObject() {
            expect('{');
            Map<String,Object> map = new HashMap<>();
            skipWhitespace();
            if (peek() == '}') { pos++; return map; }
            while (true) {
                String key = parseString();
                skipWhitespace();
                expect(':');
                Object value = parseValue();
                map.put(key, value);
                skipWhitespace();
                char c = peek();
                if (c == ',') { pos++; continue; }
                if (c == '}') { pos++; break; }
                throw new RuntimeException("Expected ',' or '}' at position " + pos);
            }
            return map;
        }

        private List<Object> parseArray() {
            expect('[');
            List<Object> list = new ArrayList<>();
            skipWhitespace();
            if (peek() == ']') { pos++; return list; }
            while (true) {
                Object value = parseValue();
                list.add(value);
                skipWhitespace();
                char c = peek();
                if (c == ',') { pos++; continue; }
                if (c == ']') { pos++; break; }
                throw new RuntimeException("Expected ',' or ']' at position " + pos);
            }
            return list;
        }

        private String parseString() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (pos < s.length()) {
                char c = s.charAt(pos++);
                if (c == '"') break;
                if (c == '\\') {
                    char esc = s.charAt(pos++);
                    switch (esc) {
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'b': sb.append('\b'); break;
                        case 'f': sb.append('\f'); break;
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case 'u':
                            int code = Integer.parseInt(s.substring(pos, pos + 4), 16);
                            sb.append((char) code);
                            pos += 4;
                            break;
                        default: sb.append(esc); break;
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        private Object parseNumber() {
            int start = pos;
            while (pos < s.length()) {
                char c = s.charAt(pos);
                if ((c >= '0' && c <= '9') || c == '-' || c == '+' || c == '.' || c == 'e' || c == 'E') {
                    pos++;
                } else {
                    break;
                }
            }
            String num = s.substring(start, pos);
            if (num.indexOf('.') >= 0 || num.indexOf('e') >= 0 || num.indexOf('E') >= 0) {
                return Double.valueOf(num);
            }
            try {
                return Integer.valueOf(num);
            } catch (NumberFormatException e) {
                return Long.valueOf(num);
            }
        }

        private Boolean parseTrue() { consume("true"); return Boolean.TRUE; }
        private Boolean parseFalse() { consume("false"); return Boolean.FALSE; }
        private Object parseNull() { consume("null"); return null; }

        private void skipWhitespace() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
                pos++;
            }
        }

        private char peek() {
            if (pos >= s.length()) throw new RuntimeException("Unexpected end of JSON");
            return s.charAt(pos);
        }

        private void expect(char expected) {
            if (peek() != expected) {
                throw new RuntimeException("Expected '" + expected + "' at position " + pos);
            }
            pos++;
        }

        private void consume(String token) {
            if (!s.startsWith(token, pos)) {
                throw new RuntimeException("Expected '" + token + "' at position " + pos);
            }
            pos += token.length();
        }
    }
}
