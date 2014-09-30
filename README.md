# Module cake-json

## Overview
A highly performant, highly flexible, stream based JSON encoding and decoding library. Provides coder-friendly APIs for
encoding and decoding arbitrary JSON data as well as out-of-the-box decoding and encoding for normal JSON data structures.

##Why another JSON library???
Why did I make another JSON library for Java? Because I felt like it.

## Basic Decoding
`JSONDecoder.parse(String json)`, `JSONDecoder.parse(Reader r)`, and `JSONDecoder.parseFile(String filePath)` provide your basic out-of-the-box decoding of any JSON type. JSON data types are mapped to Java types as outlined in the table below.

<table>
    <tr>
        <th>JSON Type</th>
        <th>Java Decoded Type</th>
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
    <tr>
        <td>Boolean</td>
        <td>java.lang.Boolean</td>
    </tr>
    <tr>
        <td>null</td>
        <td>null</td>
    </tr>
</table>

Numbers containing decimal places are decoded into either the Float or Double, and all other numbers are decoded into Integer and Long. Float and Integer are the default types, but numbers that are too large to fit are decoded into Double and Long respectively.

## Basic Encoding
Basic encoding functionality is provided by `JSONEncoder.stringify(Object data)`, `JSONEncoder.writeFile(Object data, String filePath)` and `JSONEncoder.write(Object data, Writer w)`. The table below shows the mapping of Java types to JSON types.

<table>
    <tr>
        <th>Java Type</th>
        <th>JSON Encoded Type</th>
    </tr>
    <tr>
        <td>java.lang.String</td>
        <td>String</td>
    </tr>
    <tr>
        <td>java.lang.Number</td>
        <td>Number</td>
    </tr>
    <tr>
        <td>Arrays<br/>java.util.Collection</td>
        <td>Array</td>
    </tr>
    <tr>
        <td>java.util.Map</td>
        <td>Object</td>
    </tr>
    <tr>
        <td>java.lang.Boolean</td>
        <td>Boolean</td>
    </tr>
    <tr>
        <td>null</td>
        <td>null</td>
    </tr>
    <tr>
        <td>Other</td>
        <td>String</td>
    </tr>
</table>

Map keys are converted to strings when converting to a JSON Object. Implementing `JSONWritable` allows a class to define it's own custom encoding method. Similarly `JSONObjectWriter` allows types that cannot be modified to have a custom encoding method defined.

## Advanced Decoding
Advanced or custom decoding is provided by the `parser` package, and can be done by implementing a `JSONHandlerFactory` which then delegates processing of arrays and objects to an implementation of `JSONArrayHandler` and `JSONObjectHandler` respectively. Using these classes you can implement a mechanism to process the JSON data without having to create a intermediate representation of the data via the basic decoding mechanism.

## Advanced Encoding
Advanced encoding is enabled via `JSONEncoder.object()` and `JSONEncoder.array()` instance methods which return instances of `JSONObjectBuilder` and `JSONArrayBuilder` respectively. Methods on each of those interfaces provide a mechanism for writing arrays and objects with any level of nesting without any need to create an intermediate representation.

## Minification
Minification is provided by the `JSONMinifier` class. It uses a custom advanced decoder implementation to re-encode JSON data while it is being decoded without storing any intermediate representation.

## What Else?
Might add automatic serialization and deserialization of arbitrary objects via reflection.