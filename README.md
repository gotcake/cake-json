# Module cake-json

## Overview
A highly performant, highly flexible, stream based JSON encoding and decoding library. Provides coder-friendly APIs for
encoding and decoding arbitrary JSON data as well as out-of-the-box decoding and encoding for normal JSON data structures.

## Decoding
JSONDecoder.parse, JSONDecoder.parseObject, and JSONDecoder.parseArray provide your basic out-of-the-box decoding of JSON.
JSON data types are mapped to Java types as outlined in the table below. Numbers containing decimal places are decoded into either
the Float or Double, and all other numbers are decoded into Integer and Long. Float and Integer are the default types, but
numbers that are too large to fit are decoded into Double and Long respectively.

<table>
    <tr>
        <td>JSON Type</td>
        <td>Java Type</td>
    </tr>
    <tr>
        <td>String</td>
        <td>java.lang.String</td>
    </tr>
    <tr>
        <td>Number</td>
        <td>java.lang.Float,<br/>java.lang.Double,<br/>java.lang.Integer,<br/>java.lang.Long</td>
    </tr>
    <tr>
        <td>Array</td>
        <td>java.util.ArrayList</td>
    </tr>
    <tr>
        <td>Object</td>
        <td>java.util.LinkedHashMap</td>
    </tr>
</table>


...To be continued...

