import {ternarySelection} from 'src/app/utils/ternary-selection';

describe('ternary-selection', () => {
  it('works', () => {
    expect(ternarySelection('positive', 'positive', 'negative')).toBeTrue();
    expect(ternarySelection('negative', 'positive', 'negative')).toBeFalse();
    expect(ternarySelection('other', 'positive', 'negative')).toBeNull();
  });
});
