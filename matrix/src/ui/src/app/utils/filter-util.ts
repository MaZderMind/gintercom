export class Filter<T> {
  readonly active: boolean;

  constructor(
    public readonly trigger: string,
    public readonly title: string,
    private readonly predicate: (T) => boolean
  ) {
    this.active = trigger != null;
  }

  apply(items: Array<T>): Array<T> {
    return items ? items.filter(this.predicate) : items;
  }
}

export class Filters<T> {
  private filters: Array<Filter<T>>;
  private readonly nullFilter: Filter<T>;

  constructor(noFilterTitle: string, ...filters: Array<Filter<T>>) {
    this.filters = filters;
    this.nullFilter = new Filter(null, noFilterTitle, () => true);
  }

  select(trigger: string): Filter<T> {
    return this.filters.find(f => f.trigger === trigger) || this.nullFilter;
  }
}
