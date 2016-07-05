package smolok.lib.spark

import groovy.transform.Immutable

@Immutable
class SparkSubmitResult {

    List<String> output

    List<String> errors

}
