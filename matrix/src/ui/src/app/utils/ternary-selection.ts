/**
 * Returns true if actual===positive, false if actual===negative and null otherwise.
 *
 * @param actual
 * @param positive
 * @param negative
 */
export function ternarySelection<T>(actual: T, positive: T, negative: T): boolean | null {
  if (actual === positive) {
    return true;
  } else if (actual === negative) {
    return false;
  }

  return null;
}
