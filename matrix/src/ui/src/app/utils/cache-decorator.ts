/**
 * Debounce a method
 *
 * class SearchBarComponent {
 *   constructor(private httpClient: HttpClient) {
 *   }
 *
 *   @Cache(50)
 *   getFoo(arg: bar) {
 *       return this.httpClient.get("/api/search?q=" + searchText).toPromise();
 *   }
 * }
 * </code>
 */
import * as _ from 'lodash';

interface CacheEntry {
  callArgs: IArguments;
  result: any;
}

function log(...args: any[]) {
  // nop
}

const DEFAULT_CACHE_DURATION = 150;

export function Cache(maxAgeMs = DEFAULT_CACHE_DURATION) {
  const cache: CacheEntry[] = [];

  return (target: any, key: any, descriptor: any) => {
    const oldFunc = descriptor.value;

    descriptor.value = function() {
      log('looking for cached entry for', arguments);
      const cachedEntry = cache.find(cacheEntry =>
        _.isEqual(cacheEntry.callArgs, arguments));

      if (cachedEntry) {
        log('found cached entry for', arguments);
        return cachedEntry.result;
      }

      log('executing oldFunc for', arguments);
      const executionResult = oldFunc.apply(this, arguments);
      log('executionResult for', arguments, 'is', executionResult);

      log('creating cache-entry for', arguments);
      const newCacheEntry = {callArgs: arguments, result: executionResult};
      cache.push(newCacheEntry);

      log('registering cache-result removal for', arguments);
      setTimeout(() => {
        log('removing cache-entry for', arguments);
        _.pull(cache, newCacheEntry);
      }, maxAgeMs);

      log('returning executionResult for', arguments);
      return executionResult;
    };
  };
}
