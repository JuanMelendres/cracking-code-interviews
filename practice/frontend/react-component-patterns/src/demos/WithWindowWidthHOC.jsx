import { Component } from 'react';

// F-111a: a Higher-Order Component -- a function that takes a component and
// returns a NEW component wrapping it, injecting extra props. This is the
// pre-hooks (2015-2018 era) way to share stateful behavior across
// components that otherwise share nothing.
export function withWindowWidth(WrappedComponent) {
  class WithWindowWidth extends Component {
    constructor(props) {
      super(props);
      this.state = { width: window.innerWidth };
      this.handleResize = this.handleResize.bind(this);
    }
    handleResize() {
      this.setState({ width: window.innerWidth });
    }
    componentDidMount() {
      window.addEventListener('resize', this.handleResize);
    }
    componentWillUnmount() {
      window.removeEventListener('resize', this.handleResize);
    }
    render() {
      // Injects `width` as a prop -- the wrapped component never calls
      // any hook or subscribes to anything itself.
      return <WrappedComponent {...this.props} width={this.state.width} />;
    }
  }
  WithWindowWidth.displayName = `withWindowWidth(${WrappedComponent.displayName || WrappedComponent.name || 'Component'})`;
  return WithWindowWidth;
}

function WindowWidthDisplayInner({ width }) {
  return <span data-testid="hoc-width">HOC: window width = {width}px</span>;
}

export const WindowWidthDisplayHOC = withWindowWidth(WindowWidthDisplayInner);
