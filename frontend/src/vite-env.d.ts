/// <reference types="vite/client" />

// Declaratie pentru modulele CSS
declare module "*.css" {
  const content: Record<string, string>;
  export default content;
}

// Declaratie pentru modulele SVG
declare module "*.svg" {
  import React = require("react");
  export const ReactComponent: React.FC<React.SVGProps<SVGSVGElement>>;
  const src: string;
  export default src;
}
