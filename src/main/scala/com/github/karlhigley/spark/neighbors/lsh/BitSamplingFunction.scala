package com.github.karlhigley.spark.neighbors.lsh

import java.util.Random
import scala.collection.immutable.BitSet

import org.apache.spark.mllib.linalg.{ Vector => MLLibVector, SparseVector }

/**
 *
 * References:
 *  - Gionis, Indyk, Motwanit. "Similarity Search in High Dimensions via Hashing."
 *    Very Large Data Bases, 1999.
 *
 * @see [[https://en.wikipedia.org/wiki/Locality-sensitive_hashing#Bit_sampling_for_Hamming_distance
 *          Bit sampling for Hamming Distance (Wikipedia)]]
 */
class BitSamplingFunction(val sampledBits: Array[Int]) extends LSHFunction[BitSignature] with Serializable {

  /**
   * Compute the hash signature of the supplied vector
   */
  def signature(vector: MLLibVector): BitSignature = {
    val sampled = vector.asInstanceOf[SparseVector].indices.intersect(sampledBits)
    new BitSignature(BitSet(sampled: _*))
  }

  /**
   * Build a hash table entry for the supplied vector
   */
  def hashTableEntry(id: Long, table: Int, v: MLLibVector): BitHashTableEntry = {
    BitHashTableEntry(id, table, signature(v), v)
  }

}

object BitSamplingFunction {

  /**
   * Build a random hash function, given the vector dimension
   * and signature length
   *
   * @param originalDim dimensionality of the vectors to be hashed
   * @param signatureLength the number of bits in each hash signature
   * @return randomly selected hash function from bit sampling family
   */
  def generate(originalDim: Int,
               signatureLength: Int,
               random: Random = new Random): BitSamplingFunction = {
    val indices = Array.fill(signatureLength) {
      random.nextInt(originalDim)
    }

    new BitSamplingFunction(indices)
  }

}