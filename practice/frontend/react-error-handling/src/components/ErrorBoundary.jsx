import { Component } from 'react';

// Error boundaries have NO hook equivalent as of React 19 — they must be
// class components, because getDerivedStateFromError/componentDidCatch are
// lifecycle methods with no hook counterpart. This is a real, current
// interview-relevant fact, not a legacy pattern being kept for tradition.
export default class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, info) {
    if (this.props.onError) {
      this.props.onError(error, info);
    }
  }

  handleReset = () => {
    this.setState({ hasError: false, error: null });
    if (this.props.onReset) {
      this.props.onReset();
    }
  };

  render() {
    if (this.state.hasError) {
      return this.props.fallback(this.state.error, this.handleReset);
    }
    return this.props.children;
  }
}
