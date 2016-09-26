package net.smolok.lib.endpoint

interface RequestConverter {

    boolean supports(String payload)

    Object convert(String payload)

}